# Style Guide for Jenkins Jobs

This is the style guide for Jenkins jobs at Conjur. If we stick to this our lives will be a lot easier.

## Contract

Each Jenkins job at Conjur should adhere to the following rules:

1. The job **should** be defined in the [Jenkins Job DSL](https://github.com/jenkinsci/job-dsl-plugin/wiki) and checked into this repository. Jobs created in the Jenkins UI are not guaranteed to persist. Use `_` instead of `-` in your repo names and DSL files.
2. The job **should** have a description that:
    * Describes what it does.
    * Links to the GitHub repo (if it pulls one).
    * Describes any manual promotion steps.
3. The job **should not** define the branch to build. This is covered by conventions.
4. The job **should** clear its workspace before building.
5. The job **should not** depend on Jenkins state. This means:
    * Files should never be written outside a job's workspace. No abusing `/tmp`.
    * `apt` packages should not be installed on the Jenkins system by a job.
    * Docker images should be pulled/built before trying to use them.
6. The job **should** be able to run concurrently. This means no locking up external resources in your build or tests.
7. The job's build name **should** include the branch being built. Other useful information can be added with the `setBuildName` convention method.

## jenkins.sh

Whenever possible, each job should have its build/test procedure defined in a file called `jenkins.sh`. This file should use the `-e` bash flag to exit early on error and, if cleanup is required, use an [exit trap](http://redsymbol.net/articles/bash-exit-traps/). If you're using Makefiles, invoke them from `jenkins.sh`. The only build step for your job is then a shell step running `./jenkins.sh`.

## Conventions

Common conventions are defined in this repo in `lib/conventions/Conjur.groovy`. These conventions make it easier to meet the contract defined above.

The conventions extend the DSL `Job` object with helper methods. Enable them by wrapping a job description with `use(conjur.Conventions) {}` and calling `job.<helper_function>` after its definition. Use the jobs already defined as examples.

**Methods**

* `job.applyCommonConfig()` - Covers contract `#4` and `#7`.
* `job.addGitRepo(projectRepo, triggerOnPush)` - Cover contract `#3`.
* `job.setBuildName(buildName)` - Add extra info to your build name. '#${BUILD_NUMBER} ${GIT_BRANCH}' is already set by `applyCommonConfig`.

## Parallel test suites

Multiple test suites should run in parallel if possible to keep build time down. If your projects requires this, pass an argument to `jenkins.sh` to select which tests to run. Split your tests into different Jenkins jobs and trigger them as blocking build steps, **not** post-build steps.

Example, defining multiple test jobs to run in parallel in the DSL:

```
def projectName = 'myproject'
def projectRepo = "git@github.com:conjurinc/${projectName}.git"
def testSuites = ['unit', 'functional', 'acceptance']

use(conjur.Conventions) {
  def job = job(projectName) {
    ... // description, wrappers, etc
    steps {
      downstreamParameterized {
        trigger(testSuites.join(',')) {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            gitRevision()
          }
        }
      }
      // define other steps here, to run after parallel tests
    }
    ... // artifact collection, post-build, promotions, etc
  }
  job.applyCommonConfig()
  job.addGitRepo(projectRepo)
  
  testSuites.each { -> suite
    def jobName = "${projectName}_test_${suite}"
    def job = job(jobName) {
      ... // description, wrappers, etc
      steps {
        shell("./jenkins.sh ${suite}")
      }
      ... // artifact collection, post-build, etc
    }
    job.applyCommonConfig()
    job.addGitRepo(projectRepo, false) // 2nd param tells job to not watch for changes
  }
}
```

This will create 4 jobs and link them all together.

## Views

Views should also defined in the DSL. Delivery pipelines are views. 

Browse the `views` folder in this repo for examples.
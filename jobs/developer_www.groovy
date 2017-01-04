use(conjur.Conventions) {
  def job = job('developer-www') {
    description('Conjur developer web site (developer.conjur.net)')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('results/rspec.xml')
      postBuildScripts {  // deploy to Heroku if tests pass on master
        steps {
          conditionalSteps {
            condition {
              stringsMatch('${GIT_BRANCH}', 'origin/master', false)
            }
            runner('Run')
            steps {
              downstreamParameterized {
                trigger('release-heroku') {
                  block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                  }
                  parameters {
                    predefinedProp('APP_NAME', 'developer-www-conjur')
                    currentBuild()
                    gitRevision()
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/developer-www.git')
}

# jenkins-seed

DSL-defined jobs for Conjur Jenkins

[Jenkins DSL API explorer](https://jenkinsci.github.io/job-dsl-plugin)

Jobs are written as Groovy files and placed in the `jobs` folder.

The Jenkins job [_dsl_seed](https://jenkins.conjur.net/job/_dsl_seed) will run whenever this repo is updated.

Use the [_dsl_seed_sandbox](https://jenkins.conjur.net/job/_dsl_seed_sandbox) job as a scratchpad for testing out DSL scripts as you write them.

We're also using [this plugin](https://github.com/codecentric/job-dsl-promotions-plugin) to extend the DSL for promotion workflows. This will be merged eventually into the DSL proper, but we've installed it manually for now.
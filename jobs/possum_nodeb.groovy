use(conjur.Conventions) {
  def job = job('possum_nodeb') {
    description('Possum server')

    steps {
      shell('./jenkins.sh')
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
                predefinedProp('APP_NAME', 'possum-ci-conjur')
                currentBuild()
                gitRevision()
              }
            }
          }
        }
      }
    }    
    
    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, cucumber/api/features/reports/*.xml, cucumber/policy/features/reports/*.xml')
    }

    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
    }
  }

  job.addGitRepo('git@github.com:conjurinc/possum.git')
  job.applyCommonConfig()
}

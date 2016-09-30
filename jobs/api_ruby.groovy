use(conjur.Conventions) {
  def job = job('api-ruby') {
    description('Test the Conjur Ruby client library')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }

    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
    }

    properties {
      promotions {
        promotion {
          name('Publish to Rubygems')
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger('release-rubygems') {
                block {
                  buildStepFailure('FAILURE')
                  failure('FAILURE')
                  unstable('UNSTABLE')
                }
                parameters {
                  predefinedProp('GEM_NAME', 'conjur-api')
                }
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/api-ruby.git')
  job.applyCommonConfig()
}

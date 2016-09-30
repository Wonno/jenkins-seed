use(conjur.Conventions) {
  def job = job('conjur-asset-policy') {
    description('Test the Conjur policy plugin')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }

    properties {
      promotions {
        promotion {
          name('Publish to rubygems')
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
                  predefinedProp('GEM_NAME', 'conjur-asset-policy')
                }
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/conjur-asset-policy.git')
}

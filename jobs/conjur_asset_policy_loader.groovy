use(conjur.Conventions) {
  def job = job('conjur-asset-policy-loader') {
    description('Client library for the policy loader')

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
                  predefinedProp('GEM_NAME', 'conjur-asset-policy-loader')
                }
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/conjur-asset-policy-loader.git')
  job.applyCommonConfig()
}

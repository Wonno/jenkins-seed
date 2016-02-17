use(conjur.Conventions) {
  def job = job('conjur-asset-policy-loader') {
    description('Client library for the policy loader')

    properties {
      promotions {
        promotion {
          name("Release to Rubygems")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME","conjur-asset-policy-loader")
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

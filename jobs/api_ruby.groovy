use(conjur.Conventions) {
  def job = job('api-ruby') {
    description('Test the Conjur Ruby client library')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml')
    }

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
                predefinedProp("GEM_NAME","conjur-api")
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

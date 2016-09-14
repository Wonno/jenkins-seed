use(conjur.Conventions) {
  def job = job('conjur-rack') {
    description('Conjur fork of rack webserver')

    wrappers {
      rvm('2.2.4@conjur-rack')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml')
    }

    properties {
      promotions {
        promotion {
          name("Publish to RubyGems")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME", "conjur-rack")
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/conjur-rack.git')
}

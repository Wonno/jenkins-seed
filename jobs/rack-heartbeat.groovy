use(conjur.Conventions) {
  def job = job('rack-heartbeat') {
    description('conjur-rack-heartbeat - respond to OPTIONS / with 200 OK')

    wrappers {
      rvm('2.0.0@rack-heartbeat')
    }
    
    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('report.xml')
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
                predefinedProp("GEM_NAME","conjur-rack-heartbeat")
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/rack-heartbeat.git')
  job.applyCommonConfig()
}

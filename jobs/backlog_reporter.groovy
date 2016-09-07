use(conjur.Conventions) {
  def job = job('backlog_reporter') {
    description('Gem packaging of webserver backlog detection logic')

    wrappers {
      rvm('2@backlog_reporter')
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
          name("Release to Rubygems")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME","backlog_reporter")
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/backlog-reporter.git')
  job.applyCommonConfig()
}

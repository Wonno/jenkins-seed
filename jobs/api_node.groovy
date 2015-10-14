use(conjur.Conventions) {
  def job = job('api-node') {
    description('Test the Conjur Node.js client library')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('report/xunit.xml')
    }

    properties {
      promotions {
        promotion {
          name("Release to NPM")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-npm", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("PACKAGE_NAME","conjur-api")
              }
            }
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/api-node.git')
}

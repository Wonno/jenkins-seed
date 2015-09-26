import utilities.Conjur

def job = Conjur.createStandardJob(
  this,
  'api-node',
  'Test the Conjur Node.js client library',
  'git@github.com:conjurinc/api-node.git'
)

job.with {
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

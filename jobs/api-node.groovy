import utilities.Utilities

def job = Utilities.createStandardJob(
  this,
  'api-node',
  'Test the Conjur Node.js client library',
  'git@github.com:conjurinc/api-node.git'
)

job.with {
  publishers {
    archiveJunit('report/xunit.xml')
  }
}

Utilities.addManualPromotion(
  job, 'Release to NPM', 'release-npm', 'PACKAGE_NAME', 'conjur-api'
)

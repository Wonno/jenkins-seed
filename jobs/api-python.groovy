import utilities.Utilities

def job = Utilities.createStandardJob(
  this,
  'api-python',
  'Test the Conjur Python client library',
  'git@github.com:conjurinc/api-python.git'
)

job.with {
  publishers {
    archiveJunit('artifacts/pytest.xml')
    cobertura('artifacts/coverage.xml')
    publishHtml {
      report('artifacts/htmlcov') {
        reportName('Test Coverage')
      }
    }
    violations(50) {
      pylint(10, 999, 999, 'artifacts.pylint.out')
    }
  }
}

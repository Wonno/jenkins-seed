use(conjur.Conventions) {
  def job = job('api_python') {
    description('Test the Conjur Python client library')

    steps {
      shell('./jenkins.sh')
    }

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
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/api-python.git')
}

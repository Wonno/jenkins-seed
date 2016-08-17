use(conjur.Conventions) {
  def job = job('api-python') {
    description('Test the Conjur Python client library')

    parameters {
      stringParam('DOCKER_IMAGE', 'registry.tld/conjurinc/possum', 'Possum Docker image to use for tests')
      stringParam('PULL', '1', 'Pull the latest image')
    }

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

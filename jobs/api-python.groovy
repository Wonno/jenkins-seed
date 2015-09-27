import utilities.Config

def job = job('api-python') {
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

Config.addGitRepo(job, 'git@github.com:conjurinc/api-python.git')
Config.applyCommonConfig(job)

job('api-python') {
  description('Test the Conjur Python client library')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/api-python.git')
  }

  triggers {
    githubPush()
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
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
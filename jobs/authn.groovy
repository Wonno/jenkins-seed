job('authn') {
  description('Test the Conjur authn core service')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/authn.git')
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
    archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    plotBuildData {
      plot('Performance', 'performance.csv') {
        style('line')
        yAxis('requests per second')
        csvFile('performance.csv')
      }
    }
  }
}

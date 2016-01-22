use(conjur.Conventions) {
  def job = job('ldap-server') {
    description('Test the Conjur ldap-server service')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('reports/*.xml')
      plotBuildData {
        plot('Performance', 'performance.csv') {
          style('line')
          yAxis('requests per second')
          csvFile('performance.csv')
        }
      }
      postBuildScripts {
          steps {
              shell('./publish.sh')
          }
          onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/ldap-server.git')
}

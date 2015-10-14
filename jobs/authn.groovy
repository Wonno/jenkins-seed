use(conjur.Conventions) {
  def job = job('authn') {
    description('Test the Conjur authn core service')

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
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn.git')
}

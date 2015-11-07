use(conjur.Conventions) {
  def job = job('apidocs') {
    description('Run tests against the Conjur API documentation')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('report.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/apidocs.git')
}

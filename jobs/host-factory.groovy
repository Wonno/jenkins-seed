use(conjur.Conventions) {
  def job = job('host-factory') {
    description('Build and test the Conjur host factory service')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/host-factory.git')
}

use(conjur.Conventions) {
  def job = job('core') {
    description('Test the Conjur core ...core service')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/core.git')
}

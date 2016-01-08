use(conjur.Conventions) {
  def job = job('host-factory') {
    description('host-factory - generate host identities for servers and VMs')

    wrappers {
      rvm('2.0.0@host-factory')
    }
    
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

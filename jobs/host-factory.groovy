use(conjur.Conventions) {
  def job = job('host-factory') {
    description('host-factory - generate host identities for servers and VMs')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
    
    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/host-factory.git')
}

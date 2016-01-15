use(conjur.Conventions) {
  def job = job('host-factory') {
    description('host-factory - generate host identities for servers and VMs')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
      postBuildScripts {
          steps {
              shell('./publish.sh')
          }
          onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/host-factory.git')
}

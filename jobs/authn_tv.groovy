use(conjur.Conventions) {
  def job = job('authn-tv') {
    description('Build token-vending authn')

    steps {
      shell('''
        ./jenkins.sh
      '''.stripIndent())
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
    
    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
}

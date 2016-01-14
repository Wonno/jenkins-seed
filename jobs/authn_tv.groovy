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
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
}

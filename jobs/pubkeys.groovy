use(conjur.Conventions) {
  def job = job('pubkeys') {
    description('''
      pubkeys - Manage and distribute public keys
    '''.stripIndent())

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('features/reports/*.xml')
      postBuildScripts {
          steps {
              shell('./publish.sh')
          }
          onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/pubkeys.git')
}

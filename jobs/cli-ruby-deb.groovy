use(conjur.Conventions) {
  def job = job('cli-ruby-deb') {
    description('Builds a non-Omnibus deb for the Ruby CLI')

    steps {
      shell('./build-deb.sh')
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
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

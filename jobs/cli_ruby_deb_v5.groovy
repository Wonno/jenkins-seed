use(conjur.Conventions) {
  def job = job('cli-ruby-deb-v5') {
    description('Builds pure Rubygems style non-Omnibus deb for the Ruby CLI, without using bundler')

    steps {
      shell('./build-deb.sh')
    }

    publishers {
      archiveArtifacts(artifacts)
      postBuildScripts {
        steps {
          shell('./publish.sh 5.0')
        }
        onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

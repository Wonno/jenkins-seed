use(conjur.Conventions) {
  def job = job('possum-example') {
    description('Builds the possum-example project')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      postBuildScripts {
        steps {
          shell('./publish.sh')
        }
        onlyIfBuildSucceeds(true)
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/possum-example.git')
}

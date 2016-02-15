use(conjur.Conventions) {
  def job = job('nginx') {
    description('Build custom nginx package for installation on the appliance')

    parameters {
      stringParam('DISTRIBUTION', '5.0', 'apt distribution to push package to')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      postBuildScripts {
        steps {
          shell('./publish.sh $DISTRIBUTION')
        }
        onlyIfBuildSucceeds(true)
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/nginx.git')
}

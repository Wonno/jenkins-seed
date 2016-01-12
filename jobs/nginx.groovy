use(conjur.Conventions) {
  def job = job('nginx') {
    description('Build custom nginx package for installation on the appliance')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/nginx.git')
  job.publishDebianOnSuccess()
}

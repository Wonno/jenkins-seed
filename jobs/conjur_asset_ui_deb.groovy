use(conjur.Conventions) {
  def job = job('conjur-asset-ui-deb') {
    description('Build debs for Conjur UI')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('app/*.deb')
        pattern('server/*.deb')
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo("git@github.com:conjurinc/conjur-asset-ui.git")
}

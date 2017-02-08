use(conjur.Conventions) {
  def job = job('conjur-ui') {
    description('Build new Conjur UI')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('*.deb')
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo("git@github.com:conjurinc/conjur-ui.git")
}

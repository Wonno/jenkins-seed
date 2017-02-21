use(conjur.Conventions) {
  def job = job('conjur-ui') {
    description('Build new Conjur UI')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('*.deb')
        archiveJunit('spec/reports/*.xml, client/reports/*.xml')
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo("git@github.com:conjurinc/conjur-ui.git")
  job.publishDebsOnSuccess('ui')  // debify prepends 'conjur-'
}

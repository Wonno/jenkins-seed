use(conjur.Conventions) {
  def job = job('pubkeys') {
    using('templates/conjur_service')
    description('pubkeys - Manage and distribute public keys')

    steps {
      shell('debify publish -c testing 4.6 pubkeys')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/pubkeys.git')
}

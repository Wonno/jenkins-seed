use(conjur.Conventions) {
  def job = job('pubkeys') {
    using('templates/conjur_service')
    description('pubkeys - Manage and distribute public keys')
  }
  job.addGitRepo('git@github.com:conjurinc/pubkeys.git')
}

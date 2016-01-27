use(conjur.Conventions) {
  def job = job('authn-tv') {
    using('templates/conjur_service')
    description('Build token-vending authn')
  }
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
}

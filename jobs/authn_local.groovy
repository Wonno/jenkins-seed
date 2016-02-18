use(conjur.Conventions) {
  def job = job('authn-local') {
    using('templates/conjur_service')
    description('Local authentication service')
  }
  job.addGitRepo('git@github.com:conjurinc/authn-local.git')
}

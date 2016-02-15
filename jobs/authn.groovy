use(conjur.Conventions) {
  def job = job('authn') {
    using('templates/conjur_service')
    description('Test the Conjur authn core service')
  }
  job.addGitRepo('git@github.com:conjurinc/authn.git')
}

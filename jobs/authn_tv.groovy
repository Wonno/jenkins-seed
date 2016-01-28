use(conjur.Conventions) {
  def job = job('authn-tv') {
    using('templates/conjur_service')
    description('Build token-vending authn')

    steps {
      shell('debify publish -c testing 4.6 authn-tv')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
}

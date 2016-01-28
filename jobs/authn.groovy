use(conjur.Conventions) {
  def job = job('authn') {
    using('templates/conjur_service')
    description('Test the Conjur authn core service')

    steps {
      shell('debify publish -c testing 4.6 authn')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/authn.git')
}

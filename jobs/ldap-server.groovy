use(conjur.Conventions) {
  def job = job('ldap-server') {
    using('templates/conjur_service')
    description('Test the Conjur ldap-server service')

    steps {
      shell('debify publish -c testing 4.6 ldap-server')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/ldap-server.git')
}

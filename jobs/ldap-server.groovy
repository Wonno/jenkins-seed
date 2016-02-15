use(conjur.Conventions) {
  def job = job('ldap-server') {
    using('templates/conjur_service')
    description('Test the Conjur ldap-server service')
  }
  job.addGitRepo('git@github.com:conjurinc/ldap-server.git')
}

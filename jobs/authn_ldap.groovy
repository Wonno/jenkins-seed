use(conjur.Conventions) {
  def job = job('authn_ldap') {
    using('templates/conjur_service')
    description('''
      A Conjur authenticator which calls out to an external LDAP service to bind.
      <br>
      <a href="https://github.com/conjurinc/authn-ldap/blob/master/README.md">README</a>
    '''.stripIndent())
  }
  job.addGitRepo('git@github.com:conjurinc/authn-ldap.git')
}

use(conjur.Conventions) {
  def job = job('authn_ldap') {
    description('''
      A Conjur authenticator which calls out to an external LDAP service to bind.
      <br>
      <a href="https://github.com/conjurinc/authn-ldap/blob/master/README.md">README</a>
      <hr>
      Builds Debian packages
    '''.stripIndent())

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/authn-ldap.git')
  job.applyCommonConfig()
}

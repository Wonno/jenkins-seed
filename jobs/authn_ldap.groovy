use(conjur.Conventions) {
  def job = job('authn_ldap') {
    description('''
      A Conjur authenticator which calls out to an external LDAP service to bind.
      <br>
      <a href="https://github.com/conjurinc/authn-ldap/blob/master/README.md">README</a>
    '''.stripIndent())

    steps {
      shell('''
        ./jenkins.sh
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
      postBuildScripts {
          steps {
              shell('./publish.sh')
          }
          onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn-ldap.git')
}

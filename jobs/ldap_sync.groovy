use(conjur.Conventions) {
  def job = job('ldap-sync') {
    description('Build and test ldap-sync')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/ldap-sync.git')
}

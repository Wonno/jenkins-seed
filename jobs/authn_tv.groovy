use(conjur.Conventions) {
  def job = job('authn-tv') {
    description('Build token-vending authn')

    wrappers {
      rvm('2.0.0@authn-tv')
    }

    steps {
      shell('''
        ./jenkins.sh
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
}

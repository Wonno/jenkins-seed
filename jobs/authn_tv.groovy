use(conjur.Conventions) {
  def job = job('authn-tv') {
    description('Build token-vending authn')

    steps {
      shell('''
        ./jenkins.sh

        # Remove these, no longer needed after tests have run
        rm -f conjur-authn-tv-dev_latest_amd64.deb
        rm -f conjur-authn-tv_latest_amd64.deb
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authn-tv.git')
  job.publishToArtifactory(
    'debian-local', '*.deb',
    'deb.distribution=trusty;deb.component=main;deb.architecture=amd64'
  )
}

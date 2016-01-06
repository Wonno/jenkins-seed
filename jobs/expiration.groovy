use(conjur.Conventions) {
  def job = job('expiration') {
    description('''
      A Conjur server plugin that supports variable expiration.
      <br>
      <a href="https://github.com/conjurinc/expiration/blob/master/README.md">README</a>
      <hr>
      Builds Debian packages
    '''.stripIndent())

    steps {
      shell('''
        ./jenkins.sh
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('ci/output/report/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/expiration.git')
  job.publishToArtifactory(
    'debian-local', '*.deb',
    'deb.distribution=trusty;deb.component=main;deb.architecture=amd64'
  )
}
use(conjur.Conventions) {
  def job = job('expiration') {
    description('''
      A Conjur server plugin that supports variable expiration.
      <br>
      <a href="https://github.com/conjurinc/expiration/blob/master/README.md">README</a>
      <hr>
      Builds Debian packages
    '''.stripIndent())

    wrappers {
      rvm('2.0.0@expiration')
    }

    steps {
      shell('''
        ./jenkins.sh
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('features/report/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/expiration.git')
  job.publishToArtifactory(
    'debian-local', '*.deb',
    'deb.distribution=trusty;deb.component=main;deb.architecture=amd64'
  )
}

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
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('features/report/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/expiration.git')
}

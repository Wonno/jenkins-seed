use(conjur.Conventions) {
  def job = job('expiration') {
    using('templates/conjur_service')
    description('''
      A Conjur server plugin that supports variable expiration.
      <br>
      <a href="https://github.com/conjurinc/expiration/blob/master/README.md">README</a>
      <hr>
      Builds Debian packages
    '''.stripIndent())
  }
  job.addGitRepo('git@github.com:conjurinc/expiration.git')
}

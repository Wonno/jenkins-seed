use(conjur.Conventions) {
  def job = job('evoke') {
    using('templates/conjur_service')
    description('Build and test evoke, the configuration and server management tool.')

    steps {
      shell('debify publish -c testing 4.6 evoke')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/evoke.git')
}

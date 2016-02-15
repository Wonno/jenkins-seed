use(conjur.Conventions) {
  def job = job('evoke') {
    using('templates/conjur_service')
    description('Build and test evoke, the configuration and server management tool.')
  }
  job.addGitRepo('git@github.com:conjurinc/evoke.git')
}

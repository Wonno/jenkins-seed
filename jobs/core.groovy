use(conjur.Conventions) {
  def job = job('core') {
    using('templates/conjur_service')
    description('Build and test the Conjur core ...core service')
  }
  job.addGitRepo('git@github.com:conjurinc/core.git')
}

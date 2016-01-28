use(conjur.Conventions) {
  def job = job('core') {
    using('templates/conjur_service')
    description('Build and test the Conjur core ...core service')

    steps {
      shell('debify publish -c testing 4.6 core')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/core.git')
}

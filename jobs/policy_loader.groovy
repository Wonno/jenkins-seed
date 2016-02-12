use(conjur.Conventions) {
  def job = job('policy-loader') {
    using('templates/conjur_service')
    description('Build the policy loader service')

    steps {
      shell('debify publish -c testing 4.6 policy-loader')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/policy-loader.git')
}

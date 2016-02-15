use(conjur.Conventions) {
  def job = job('policy-loader') {
    using('templates/conjur_service')
    description('Build the policy loader service')
  }
  job.addGitRepo('git@github.com:conjurinc/policy-loader.git')
}

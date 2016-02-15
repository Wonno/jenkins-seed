use(conjur.Conventions) {
  def job = job('audit') {
    using('templates/conjur_service')
    description('Test the Conjur audit core service')
  }
  job.addGitRepo('git@github.com:conjurinc/audit.git')
}

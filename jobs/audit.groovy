use(conjur.Conventions) {
  def job = job('audit') {
    using('templates/conjur_service')
    description('Test the Conjur audit core service')

    steps {
      shell('debify publish -c testing 4.6 audit')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/audit.git')
}

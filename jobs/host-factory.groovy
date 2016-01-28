use(conjur.Conventions) {
  def job = job('host-factory') {
    using('templates/conjur_service')
    description('host-factory - generate host identities for servers and VMs')

    steps {
      shell('debify publish -c testing 4.6 host-factory')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/host-factory.git')
}

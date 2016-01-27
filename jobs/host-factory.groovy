use(conjur.Conventions) {
  def job = job('host-factory') {
    using('templates/conjur_service')
    description('host-factory - generate host identities for servers and VMs')
  }
  job.addGitRepo('git@github.com:conjurinc/host-factory.git')
}

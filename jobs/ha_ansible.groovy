use(conjur.Conventions) {
  def job = job('ha-ansible') {
    description('Test Conjur HA Ansible scripts')

    steps {
      shell('./jenkins.sh')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurdemos/ha-ansible.git')
}

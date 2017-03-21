use(conjur.Conventions) {
  def job = job('sshd-service') {
    description('Tests the sshd-service cookbook')

    steps {
      shell('./jenkins.sh')
    }
  }

  job.addGitRepo('git@github.com:conjur-cookbooks/sshd-service.git')
  job.applyCommonConfig(label: 'executor-v2')
}

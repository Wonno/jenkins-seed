use(conjur.Conventions) {
  def job = job('training') {
    description('Conjur training demos')

    steps {
      shell('./jenkins.sh')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/training.git')
  job.applyCommonConfig(label: 'executor-v2')
}

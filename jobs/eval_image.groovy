use(conjur.Conventions) {
  def job = job('training-image') {
    description('Conjur training image')

    steps {
      shell('./jenkins.sh')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/training-image.git')
  job.applyCommonConfig(label: 'executor-v2')
}

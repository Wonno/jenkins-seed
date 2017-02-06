use(conjur.Conventions) {
  def job = job('eval-image') {
    description('Conjur evaluation image')

    steps {
      shell('./jenkins.sh')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/eval-image.git')
  job.applyCommonConfig()
}

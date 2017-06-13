use(conjur.Conventions) {
  def job = job('possum_website') {
    description('Possum Website')

    steps {
      shell('./website.sh')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/possum.git', false)
  job.applyCommonConfig()
}

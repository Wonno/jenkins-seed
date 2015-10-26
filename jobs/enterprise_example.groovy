use(conjur.Conventions) {
  def job = job('enterprise-example') {
    description('Build and test enterprise-example')

    steps {
      shell('./jenkins.sh')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurdemos/enterprise-example.git')
}

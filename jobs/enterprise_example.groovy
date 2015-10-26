use(conjur.Conventions) {
  def job = job('enterprise-example') {
    description('Build and test enterprise-example')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurdemos/enterprise-example.git')
}

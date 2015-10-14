use(conjur.Conventions) {
  def job = job('audit-sqs-migrator') {
    description('Build and test the SQS migrator')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml,features/reports/*.xml,integration_features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/audit-sqs-migrator.git')
}
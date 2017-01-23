use(conjur.Conventions) {
  def job = job('zendesk_report') {
    description('Emails a Zendesk status report at 5am ET every day')

    triggers {
        cron('00 9 * * *')
    }
    steps {
      shell('./run_report.sh')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/zendesk_report.git', false)
}

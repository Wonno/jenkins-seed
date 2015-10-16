use(conjur.Conventions) {
  def job = job('zendesk_report') {
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

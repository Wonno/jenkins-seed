use(conjur.Conventions) {
  def job = job('10factorci') {
    steps {
      downstreamParameterized {
        trigger('release_heroku') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            predefinedProp('APP_NAME', 'tenfactorci-conjur')
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/tenfactorci.git', true, '*/master')
}
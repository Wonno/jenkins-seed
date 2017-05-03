use(conjur.Conventions) {
  def job = job('appliance-docker-api-acceptance-v2') {
    description('Run API acceptance tests on Docker Conjur')

    parameters {
      stringParam('IMAGE', '', 'Appliance image id to test. Required.')
    }

    concurrentBuild()
    throttleConcurrentBuilds {
      categories(['resource-intensive'])
    }

    steps {
      shell('./test_api_acceptance.sh $IMAGE')
    }

    publishers {
      archiveJunit('ci/output/report/api-acceptance/*.xml')
      archiveArtifacts('ci/output/**')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
  job.applyCommonConfig(notifyRepeatedFailure: true, concurrent: false, label: 'executor-v2')
  job.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="APPLIANCE_IMAGE_TAG"}')
}

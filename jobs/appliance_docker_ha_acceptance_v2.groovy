use(conjur.Conventions) {
  def job = job('appliance-docker-ha-acceptance-v2') {
    description('Run HA acceptance tests on Docker Conjur')

    parameters {
      stringParam('IMAGE', '', 'Appliance image id to test. Required.')
    }

    concurrentBuild()
    throttleConcurrentBuilds {
      categories(['resource-intensive'])
    }

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.1.5@appliance-docker-ha-acceptance && export > rvm.env"')
      shell('exit 0')
    }

    publishers {
      archiveJunit('ci/output/report/ha-acceptance/*.xml')
      archiveArtifacts('ci/output/**')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
  job.applyCommonConfig(notifyRepeatedFailure: true, concurrent: false, label: 'executor-v2')
  job.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="APPLIANCE_IMAGE_TAG"}')
}

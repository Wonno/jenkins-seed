use(conjur.Conventions) {
  def job = job('appliance-docker-ami') {
    description('Build an AMI that contains Docker Conjur')

    parameters {
      stringParam('APPLIANCE_IMAGE', 'registry.tld/conjur-appliance', 'Appliance image id to test. Required.')
      stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
    }

    wrappers {
      timeout {
        noActivity(3600) // kill build after 60min of no activity
      }
    }

    steps {
      shell('./jenkins.sh $APPLIANCE_IMAGE $APPLIANCE_IMAGE_TAG')
    }

    publishers {
      archiveArtifacts('ami-*')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/appliance-docker.git', false)
  job.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="APPLIANCE_IMAGE_TAG"}')
}

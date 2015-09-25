job('appliance-docker-ami') {
  description('Build an AMI that contains Docker Conjur')
  label('docker && slave')
  logRotator(30, -1, -1, 30)

  scm {
    git('git@github.com:conjurinc/appliance-docker.git')
  }

  parameters {
    stringParam('APPLIANCE_IMAGE', 'registry.tld:80/conjur-appliance', 'Appliance image id to test. Required.')
    stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('./jenkins.sh $APPLIANCE_IMAGE $APPLIANCE_IMAGE_TAG')
  }

  publishers {
    slackNotifications {
      projectChannel('#jenkins')
      notifyFailure()
      notifyUnstable()
      notifyBackToNormal()
    }
  }
}

job('appliance-docker-ami') {
  description('Build an AMI that contains Docker Conjur')
  label('docker && slave')
  logRotator(30, -1, -1, 30)

  scm {
    git('git@github.com:conjurinc/appliance-docker.git')
  }

  parameters {
    stringParam('IMAGE_TAG', 'latest', 'Appliance image tag to test. Required.')
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('./jenkins.sh')
  }

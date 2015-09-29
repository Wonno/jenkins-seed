import utilities.Config

def job = job('appliance-docker-ami') {
  description('Build an AMI that contains Docker Conjur')
  concurrentBuild()

  parameters {
    stringParam('APPLIANCE_IMAGE', 'registry.tld/conjur-appliance', 'Appliance image id to test. Required.')
    stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
  }

  steps {
    shell('./jenkins.sh $APPLIANCE_IMAGE $APPLIANCE_IMAGE_TAG')
  }
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance-docker.git', false)
Config.applyCommonConfig(job)

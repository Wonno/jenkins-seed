import utilities.Config

// Build flow for Conjur Docker appliance
def job = buildFlowJob('appliance-docker-flow') {
  description('Builds Docker image, tests it and creates an AMI')
  concurrentBuild()

  buildFlow('''
    b = build("appliance-docker-build", BRANCH: params["BRANCH"])

    def imageTag = "jenkins-appliance-docker-build-\${b.build.id}"

    parallel(
      { build("appliance-docker-api-acceptance", APPLIANCE_IMAGE_TAG: imageTag) },
      { build("appliance-docker-ha-acceptance", APPLIANCE_IMAGE_TAG: imageTag) }
    )

    //build("appliance-docker-ami", APPLIANCE_IMAGE_TAG: imageTag)
  '''.stripIndent())
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git')
Config.applyCommonConfig(job)

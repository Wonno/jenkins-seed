// Build flow for Conjur Docker appliance
buildFlowJob('appliance-docker-flow') {
  description('Builds Docker image, tests it and creates an AMI')
  logRotator(30, -1, -1, 5)
  concurrentBuild()

  parameters {
    stringParam('BRANCH', '', 'Git branch or SHA of the appliance repo to build. Not required.')
  }

  scm {
    git {
      remote {
        url('git@github.com:conjurinc/appliance.git')
      }
      branch('$BRANCH')
    }
  }

  triggers {
    githubPush()
  }

  buildFlow('''
    b = build("appliance-docker-build", BRANCH: params["BRANCH"])

    def imageTag = "jenkins-appliance-docker-build-\${b.build.id}"

    parallel(
      { build("appliance-docker-api-acceptance", APPLIANCE_IMAGE_TAG: imageTag) },
      { build("appliance-docker-ha-acceptance", APPLIANCE_IMAGE_TAG: imageTag) }
    )

    //build("appliance-docker-ami", APPLIANCE_IMAGE_TAG: imageTag)
  '''.stripIndent())

  publishers {
    slackNotifications {
      projectChannel('#jenkins')
      notifyFailure()
      notifyUnstable()
      notifyBackToNormal()
    }
  }
}

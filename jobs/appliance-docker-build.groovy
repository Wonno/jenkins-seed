// Build the Conjur container
job('appliance-docker-build') {
  description('Build the Conjur Docker container')
  label('docker && slave')
  logRotator(-1, 30, -1, 30)

  parameters {
    stringParam('BRANCH', '', 'Git branch or SHA to build. Not required.')
  }

  scm {
    git {
      remote {
        url('git@github.com:conjurinc/appliance.git')
      }
      branch('$BRANCH')
    }
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    rvm('2.1.5@appliance-docker-build')
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('''
      #!/bin/bash -e
      bundle install
      ./ci/bin/jenkins-docker-build $CONJUR_DOCKER_REGISTRY $BUILD_TAG
    '''.stripIndent())
  }

  publishers {
    archiveArtifacts('ci/output/*')
    downstreamParameterized {
      trigger('appliance-docker-api-acceptance') {
        condition('SUCCESS')
        parameters {
          gitRevision()
        }
      }
      trigger('appliance-docker-ha-acceptance') {
        condition('SUCCESS')
        parameters {
          predefinedProp('APPLIANCE_IMAGE', '$CONJUR_DOCKER_REGISTRY/conjur-appliance')
          predefinedProp('APPLIANCE_IMAGE_TAG','$BUILD_TAG')
          gitRevision()
        }
      }
    }
    slackNotifications {
      projectChannel('#jenkins')
      notifyFailure()
      notifyUnstable()
      notifyBackToNormal()
    }
  }

  properties {
    promotions {
      promotion {
        name('Acceptance')
        icon('star-green')
        conditions {
          // def downstream(Boolean evenIfUnstable, String jobs)
          downstream(false, 'appliance-docker-ha-acceptance,appliance-docker-api-acceptance')
        }
      }
    }
  }
}
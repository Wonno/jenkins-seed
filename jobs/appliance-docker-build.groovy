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
    slackNotifications {
      projectChannel('#jenkins')
      notifyFailure()
      notifyUnstable()
      notifyBackToNormal()
    }
  }
}
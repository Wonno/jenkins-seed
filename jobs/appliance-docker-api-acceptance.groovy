job('appliance-docker-api-acceptance') {
  description('Run API acceptance tests on Docker Conjur')
  label('docker && slave')
  logRotator(30, -1, -1, 5)
  concurrentBuild()

  parameters {
    stringParam('APPLIANCE_IMAGE', 'registry.tld:80/conjur-appliance', 'Appliance image id to test. Required.')
    stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
    stringParam('BRANCH', 'docker', 'Git branch or SHA to build.')
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
    rvm('2.1.5@appliance-docker-api-acceptance')
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('''
      #!/bin/bash -e

      bundle install

      ./ci/bin/api-acceptance --log-level debug -i $APPLIANCE_IMAGE -t $APPLIANCE_IMAGE_TAG
    '''.stripIndent())
  }

  publishers {
    archiveJunit('ci/output/report/api-acceptance/*.xml')
    archiveArtifacts('ci/output/**')
    slackNotifications {
      projectChannel('#jenkins')
      notifyFailure()
      notifyUnstable()
      notifyBackToNormal()
    }
  }
}

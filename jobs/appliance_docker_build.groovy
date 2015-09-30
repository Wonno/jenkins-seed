import utilities.Config

def job = job('appliance_docker_build') {
  description('Build the Conjur Docker container')
  concurrentBuild()

  wrappers {
    rvm('2.1.5@appliance-docker-build')
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
      trigger('appliance-docker-api-acceptance, appliance-docker-ha-acceptance') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          gitRevision()
          predefinedProp('APPLIANCE_IMAGE_TAG', '$BUILD_TAG')
        }
      }
    }
  }

  properties {
    promotions {
      promotion {
        name("Tests passed")
        icon('star-green')
        conditions {
          downstream(false, 'appliance-docker-api-acceptance, appliance-docker-ha-acceptance')
        }
        actions {
          downstreamParameterized {
            trigger('docker_tag_and_push') {
              predefinedProp('IMAGE_NAME', 'registry.tld/conjur-appliance')
              predefinedProp('IMAGE_TAG_CURRENT', 'jenkins-$PROMOTED_JOB_NAME-$PROMOTED_NUMBER')
              predefinedProp('IMAGE_TAG_NEW', '4.5.rc$PROMOTED_NUMBER')
            }
          }
        }
      }
    }
  }
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git')
Config.applyCommonConfig(job)

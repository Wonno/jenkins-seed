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

  def testJobs = ['appliance-docker-api-acceptance', 'appliance-docker-ha-acceptance']

  properties {
    promotions {
      testJobs.each { testJob ->
        promotion {
          name("PASSED ${testJob}")
          icon("star-green")
          conditions {
            downstream(false, testJob)
          }
        }
      }
      promotion {
        name("Create AMI")
        icon("star-gold")
        conditions {
          manual('')
        }
        actions {
          downstreamParameterized {
            trigger("appliance-docker-ami", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
              predefinedProp('APPLIANCE_IMAGE_TAG', 'jenkins-$PROMOTED_JOB_NAME-$PROMOTED_NUMBER')
            }
          }
        }
      }
    }
  }
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git')
Config.applyCommonConfig(job)

def applianceVersion = '4.5.0'

use(conjur.Conventions) {
  def job = job('appliance-docker-build') {
    description('''
      Starter job for the
      <a href="/view/Docker%20Appliance%20Pipeline/">
        Conjur Docker appliance pipeline
      </a>

      <hr>
      <strong>Promotion</strong>
        <ul>
          <li>
            Autopromotion happens when downstream tests pass.
            <br>This is signified by a blue star.
          </li>
          <li>
            To create an AMI, click on a build with a blue star, go to Promotion Status, and Approve "Release AMI".
            <br>This is signified by a green star.
          </li>
      </ul>
    '''.stripIndent())
    concurrentBuild()

    parameters {
      stringParam('SERVICE_BRANCH', 'integration', 'Branch of core services to pull in.')
      stringParam('EVOKE_BRANCH', 'master', 'Branch of evoke to pull in.')
    }

    wrappers {
      rvm('2.1.5@appliance-docker-build')
    }

    steps {
      shell('''
        cat << PARAMS > roles/build-parameters.json
          {
            "name": "build-parameters",
            "chef_type": "role",
            "json_class": "Chef::Role",
            "override_attributes": {
              "conjur": {
                "branch": \"${SERVICE_BRANCH}\"
              },
              "evoke": {
                "branch": \"${EVOKE_BRANCH}\"
              }
            }
          }
        PARAMS
      '''.stripIndent())

      shell('''
        #!/bin/bash -e
        bundle install
        ./ci/bin/jenkins-docker-build $CONJUR_DOCKER_REGISTRY $BUILD_TAG
      '''.stripIndent())

      downstreamParameterized {
        trigger('appliance-docker-api-acceptance, appliance-docker-ha-acceptance') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            gitRevision()
            predefinedProp('APPLIANCE_IMAGE_TAG', '$BUILD_TAG')
          }
        }
      }

      downstreamParameterized {
        trigger('docker_tag_and_push') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            predefinedProp('IMAGE_NAME', 'registry.tld/conjur-appliance')
            predefinedProp('IMAGE_TAG_CURRENT', '$BUILD_TAG')
            predefinedProp('IMAGE_TAG_NEW', "${applianceVersion}-c\$BUILD_NUMBER")
          }
        }
      }
    }

    publishers {
      archiveArtifacts('ci/output/*')
    }

    properties {
      promotions {
        promotion {
          name('Release AMI')
          icon('star-green')
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger('appliance-docker-ami') {
                parameters {
                  predefinedProp('APPLIANCE_IMAGE_TAG', "${applianceVersion}-\$PROMOTED_NUMBER")
                }
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/appliance.git')
  job.setBuildName([
    '#${BUILD_NUMBER} ${GIT_BRANCH}: ',
    applianceVersion,
    '-c${BUILD_NUMBER}',
    ', services: ${ENV,var="SERVICE_BRANCH"}',
    ', evoke: ${ENV,var="EVOKE_BRANCH"}'
  ].join())
}

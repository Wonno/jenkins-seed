import utilities.Config

def applianceVersion = '4.5.0'

def job = job('appliance_docker_build') {
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
        icon('star-blue')
        conditions {
          downstream(false, 'appliance-docker-api-acceptance, appliance-docker-ha-acceptance')
        }
        actions {
          downstreamParameterized {
            trigger('docker_tag_and_push') {
              parameters {
                currentBuild()
                predefinedProp('IMAGE_NAME', 'registry.tld/conjur-appliance')
                predefinedProp('IMAGE_TAG_CURRENT', 'jenkins-$PROMOTED_JOB_NAME-$PROMOTED_NUMBER')
                predefinedProp('IMAGE_TAG_NEW', "${applianceVersion}-\$PROMOTED_NUMBER")
              }
            }
          }
        }
      }
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

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git')
Config.applyCommonConfig(job)
Config.setBuildName(job, '#${BUILD_NUMBER} ${GIT_BRANCH}: ' + applianceVersion + '-${BUILD_NUMBER}')

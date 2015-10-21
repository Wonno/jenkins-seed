// Sets up the main job and downstream test jobs for the Conjur UI
def mainJobName = 'conjur_asset_ui'
def repoUrl = 'git@github.com:conjurinc/conjur-asset-ui.git'

def testJobs = [
  [name: "${mainJobName}_test_frontend", script: './test_frontend.sh'],
  [name: "${mainJobName}_test_backend", script: './test_backend.sh'],
  [name: "${mainJobName}_test_acceptance", script: './test_acceptance.sh']
]
// Save this in the format downstream expects
def testJobNames = ['name'].collect{testJobs[it]}[0].join(',')

use(conjur.Conventions) {
  def mainJob = job(mainJobName) {
    description('''
      Starter job for the
      <a href="/view/UI%20Pipeline/">UI pipeline</a>
      <hr>
      <strong>Promotion</strong>
      <ul>
        <li>
          When tests pass, image is automatically pushed to DockerHub
          <a href="https://hub.docker.com/r/conjurinc/conjur-ui-dev/">conjur-ui-dev</a>.
        </li>
        <li>
          Manually push to DockerHub
          <a href="https://hub.docker.com/r/conjurinc/conjur-ui/">conjur-ui</a>
          with "Push to DockerHub prod" promotion
        </li>
      </ul>
    '''.stripIndent())
    concurrentBuild()

    steps {
      shell('echo "APP_VERSION:$(cat app/package.json | jsonfield version)" > env.properties')
      environmentVariables {
        propertiesFile('env.properties')
      }
      downstreamParameterized {
        trigger(testJobNames) {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            gitRevision()
          }
        }
      }
      conditionalSteps {
        condition {
          stringsMatch('${ENV, var="GIT_BRANCH"}', 'origin/master', false)
        }
        runner('Run')
        steps {
          downstreamParameterized {
            trigger('release_dockerhub') {
              block {
                buildStepFailure('FAILURE')
                failure('FAILURE')
                unstable('UNSTABLE')
              }
              parameters {
                propertiesFile('env.properties')
                predefinedProp('DOCKER_LOCAL_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_LOCAL_TAG', '$BUILD_NUMBER')
                predefinedProp('DOCKER_REMOTE_IMAGE', 'conjur-ui-dev')
                predefinedProp('DOCKER_REMOTE_TAG', '$APP_VERSION-rc$BUILD_NUMBER')
              }
            }
          }
          downstreamParameterized {
            trigger("${mainJobName}_deploy") {
              block {
                buildStepFailure('FAILURE')
                failure('FAILURE')
                unstable('UNSTABLE')
              }
              parameters {
                propertiesFile('env.properties')
                predefinedProp('IMAGE_TAG', '$APP_VERSION-rc$BUILD_NUMBER')
              }
            }
          }
        }
      }
    }

    publishers {
      archiveArtifacts('env.properties')
    }

    properties {
      promotions {
        promotion {
          name("Push to DockerHub prod")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release_dockerhub", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                propertiesFile('env.properties')
                predefinedProp('DOCKER_LOCAL_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_LOCAL_TAG', '$BUILD_NUMBER')
                predefinedProp('DOCKER_REMOTE_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_REMOTE_TAG', '$APP_VERSION')
              }
            }
          }
        }
      }
    }
  }
  mainJob.applyCommonConfig()
  mainJob.addGitRepo(repoUrl)

  testJobs.each { testJob ->
    def j = job(testJob['name']) {
      description("Runs ${testJob['script']}")
      concurrentBuild()

      steps {
        shell(testJob['script'])
      }
    }
    j.addGitRepo(repoUrl, false)
    j.applyCommonConfig()
  }

  def deployJob = job("${mainJobName}_deploy") {
    description('Deploy the Conjur UI to Elastic Beanstack env')

    parameters {
      stringParam('IMAGE_TAG', '', 'Tag of the UI to deploy')
    }

    steps {
      shell('cd deploy && ./deploy.sh $IMAGE_TAG')
    }
  }
  deployJob.applyCommonConfig()
  deployJob.addGitRepo(repoUrl, false)
  deployJob.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="IMAGE_TAG"}')
}

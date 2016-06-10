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
          When tests pass on master, the image is automatically pushed to DockerHub
          <a href="https://hub.docker.com/r/conjurinc/conjur-ui-dev/">conjur-ui-dev</a>
          and deployed to
          <a href="https://conjur-ui.itci.conjur.net/ui">https://conjur-ui.itci.conjur.net/ui</a>.
        </li>
        <li>
          Manually push to DockerHub
          <a href="https://hub.docker.com/r/conjurinc/conjur-ui/">conjur-ui</a>
          with "Push to DockerHub prod" promotion
        </li>
      </ul>
    '''.stripIndent())
    concurrentBuild()
    throttleConcurrentBuilds {
      maxPerNode(1)
    }

    steps {
      shell('''
        version=$(cat app/package.json | jsonfield version)-$(git rev-parse --short $GIT_COMMIT)-$BUILD_NUMBER
        echo "UI_VERSION:$version" >> env.properties
        touch UI_VERSION=$version
      '''.stripIndent())
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
            propertiesFile('env.properties')
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
                buildStepFailure('UNSTABLE')
                failure('UNSTABLE')
                unstable('UNSTABLE')
              }
              parameters {
                propertiesFile('env.properties')
                predefinedProp('DOCKER_LOCAL_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_LOCAL_TAG', '$UI_VERSION')
                predefinedProp('DOCKER_REMOTE_IMAGE', 'conjur-ui-dev')
                predefinedProp('DOCKER_REMOTE_TAG', '$UI_VERSION')
              }
            }
          }
          downstreamParameterized {
            trigger("${mainJobName}_deploy") {
              block {
                buildStepFailure('UNSTABLE')
                failure('UNSTABLE')
                unstable('UNSTABLE')
              }
              parameters {
                propertiesFile('env.properties')
                currentBuild()
                gitRevision()
              }
            }
          }
        }
      }
    }

    publishers {
      archiveArtifacts('env.properties, UI_VERSION=*')
    }

    configure { project ->
      project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
        projectNameList {
          string mainJobName
        }
      }
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
            copyArtifacts('$PROMOTED_JOB_NAME') {
              includePatterns('env.properties')
              buildSelector {
                buildNumber('$PROMOTED_NUMBER')
              }
            }
            downstreamParameterized {
              trigger("release_dockerhub", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                propertiesFile('env.properties')
                predefinedProp('DOCKER_LOCAL_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_LOCAL_TAG', '$UI_VERSION')
                predefinedProp('DOCKER_REMOTE_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_REMOTE_TAG', '$UI_VERSION')
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
      throttleConcurrentBuilds {
        maxPerNode(1)
      }

      parameters {
        stringParam('UI_VERSION', '', 'UI version to tag and push')
      }

      steps {
        shell(testJob['script'])
      }

      if (testJob['name'] == "${mainJobName}_test_backend") {
        publishers {
          cobertura('reports/*coverage.xml') {
            failNoReports(false)
          }

          archiveJunit('reports/*report.xml')
        }
      } else if (testJob['name'] == "${mainJobName}_test_frontend") {
        publishers {
          cobertura('reports/*clover.xml') {
            failNoReports(false)
          }
        }
      } else if (testJob['name'] == "${mainJobName}_test_acceptance") {
        publishers {
          archiveXUnit {
            jUnit {
              pattern('build/test/*.xml')
              skipNoTestFiles()
            }
            failedThresholds {
              unstable(0)
              unstableNew(0)
              failure(50)
              failureNew(50)
            }
            skippedThresholds {
              unstable(0)
              unstableNew(0)
              failure(0)
              failureNew(0)
            }
            thresholdMode(ThresholdMode.PERCENT)
          }
        }
      }
    }

    j.addGitRepo(repoUrl, false)
    j.applyCommonConfig()
    j.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="UI_VERSION"}')
  }

  def deployJob = job("${mainJobName}_deploy") {
    description('Deploy the Conjur UI to Elastic Beanstack env')

    parameters {
      stringParam('UI_VERSION', '', 'Tag of the UI to deploy')
    }

    steps {
      shell('cd deploy && ./deploy.sh $UI_VERSION')
    }
  }
  deployJob.applyCommonConfig()
  deployJob.addGitRepo(repoUrl, false)
  deployJob.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="UI_VERSION"}')
}

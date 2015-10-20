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
        <li>Manually promote to DockerHub with "Push to DockerHub prod" promotion</li>
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
                predefinedProp('DOCKER_IMAGE', 'conjur-ui')
                predefinedProp('DOCKER_TAG', '$APP_VERSION')
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
}

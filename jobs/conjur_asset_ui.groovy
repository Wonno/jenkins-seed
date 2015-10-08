// Sets up the main job and downstream test jobs for the Conjur UI
import utilities.Config

def mainJobName = 'conjur_asset_ui'
def repoUrl = 'git@github.com:conjurinc/conjur-asset-ui.git'

def testJobs = [
  [name: "${mainJobName}_test_frontend", script: './test_frontend.sh'],
  [name: "${mainJobName}_test_backend", script: './test_backend.sh'],
  [name: "${mainJobName}_test_acceptance", script: './test_acceptance.sh']
]
// Save this in the format downstream expects
def testJobNames = ['name'].collect{testJobs[it]}[0].join(',')

def mainJob = job(mainJobName) {
  description('''
    Starter job for the
    <a href="/view/UI%20Pipeline/">
      UI pipeline
    </a>

    <hr>
    <strong>Promotion</strong>
    <ul>
      <li>TODO</li>
    </ul>
  '''.stripIndent())
  concurrentBuild()

  steps {
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
}
Config.addGitRepo(mainJob, repoUrl)
Config.applyCommonConfig(mainJob)

testJobs.each { testJob ->
  def j = job(testJob['name']) {
    description("Runs ${testJob['script']}")
    concurrentBuild()

    steps {
      shell(testJob['script'])
    }
  }
  Config.addGitRepo(j, repoUrl, false)
  Config.applyCommonConfig(j)
}

// Sets up the main job and downstream test jobs for the Conjur UI
import utilities.Config

def mainJobName = 'conjur_asset_ui'
def repoUrl = 'git@github.com:conjurinc/conjur-asset-ui.git'

def testJobs = [
  "${mainJobName}_test_frontend",
  "${mainJobName}_test_backend",
  "${mainJobName}_test_acceptance"
]

def mainJob = job(mainJobName) {
  downstreamParameterized {
    trigger(testJobs.join(',')) {
      condition('SUCCESS')
      parameters {
        currentBuild()
        gitRevision()
      }
    }
  }
}
Config.addGitRepo(mainJob, repoUrl)
Config.applyCommonConfig(mainJob)

testJobs.each { testJob ->
  def j = job(testJob) {

  }
  Config.addGitRepo(j, repoUrl, false)
  Config.applyCommonConfig(j)
}

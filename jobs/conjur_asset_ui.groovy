// Sets up the main job and downstream test jobs for the Conjur UI
import utilities.Config

def testJobs = ['test_frontend', 'test_backend', 'test_acceptance']

def mainJob = job('conjur_asset_ui') {

}
Config.addGitRepo(mainJob, 'git@github.com:conjurinc/conjur-asset-ui.git')
Config.applyCommonConfig(mainJob)

testJobs.each { testJob ->
  def j = job("conjur_asset_ui_${testJob}") {

  }
  Config.addGitRepo(j, 'git@github.com:conjurinc/conjur-asset-ui.git', false)
  Config.applyCommonConfig(j)
}

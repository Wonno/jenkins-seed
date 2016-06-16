// cron jobs that trigger other builds on a schedule

def appliance_branches  = [
  [name: 'master', cron: 'H 5 * * *'],  // every day, 1am EST
  [name: 'v4.7',   cron: 'H 5 */7 * *'] // every week, 1am EST
]
def service_branches  = [
  [name: 'master', cron: 'H 4 * * *'],  // every day, 12am EST
  [name: 'v4.7',   cron: 'H 4 */7 * *'] // every week, 12am EST
]

// Create a __cron folder to hold jobs
def folderName = '__cron'
folder(folderName) {
  description('cron jobs, run periodically')
}

// Appliance builds
def applianceJobName = 'appliance-docker-build'
appliance_branches.each { appliance_branch ->
  def j = job("${folderName}/${applianceJobName}-${appliance_branch.name}") {
    description("Triggers job ${applianceJobName} on branch ${appliance_branch.name}")
    triggers {
      cron(appliance_branch.cron)
    }
    steps {
      downstreamParameterized {
        trigger(applianceJobName) {
          parameters {
            predefinedProp('BRANCH', appliance_branch.name)
          }
        }
      }
    }
  }
  j.applyCommonConfig()
}

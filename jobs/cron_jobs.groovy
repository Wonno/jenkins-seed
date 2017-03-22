// cron jobs that trigger other builds on a schedule
import conjur.Appliance

def cron_weekly = 'H H */7 * *'
def cron_daily = 'H H * * *'

def appliance_branches  = [
  [name: 'master', cron: cron_daily],
  [name: 'v4.8',   cron: cron_weekly],
  [name: 'v4.7',   cron: cron_weekly]
]
def service_branches  = [
  [name: 'master', cron: cron_daily],
  [name: 'v4.8',   cron: cron_weekly],
  [name: 'v4.7',   cron: cron_weekly]
]

def other_cron_jobs = [
  [name: 'conjur-cookbook', cron: cron_weekly]
]

// Create a __cron folder to hold jobs
def folderName = '__cron'
folder(folderName) {
  description('cron jobs, run periodically')
}

// Appliance builds
def applianceJobName = 'appliance-docker-build'
use(conjur.Conventions) {
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
    j.applyCommonConfig(label: 'master')  // trigger the build from the master
  }
}

// Service builds
use(conjur.Conventions) {
  service_branches.each { service_branch ->
    Appliance.getServices().each {service ->
      def j = job("${folderName}/${service}-${service_branch.name}") {
        description("Triggers job ${service} on branch ${service_branch.name}")
        triggers {
          cron(service_branch.cron)
        }
        steps {
          downstreamParameterized {
            trigger(service) {
              parameters {
                predefinedProp('BRANCH', service_branch.name)
              }
            }
          }
        }
      }
      j.applyCommonConfig(label: 'master')  // trigger the build from the master
    }
  }
}

// Other cron jobs, various jobs that need to run on a schedule
use(conjur.Conventions) {
  other_cron_jobs.each { cron_job ->
    def j = job("${folderName}/${cron_job.name}") {
      description("Triggers job ${cron_job.name} to run regularly on master branch")
      triggers {
        cron(cron_job.cron)
      }
      steps {
        downstreamParameterized {
          trigger(cron_job.name) {
            parameters {
              predefinedProp('BRANCH', 'master')
            }
          }
        }
      }
    }
    j.applyCommonConfig(label: 'master')  // trigger the build from the master
  }
}

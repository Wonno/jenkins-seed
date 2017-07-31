// cron jobs that trigger other builds on a schedule
import conjur.Appliance

def cron_weekly = 'H H */7 * *'
def cron_daily = 'H H * * *'

def service_branches  = [
  [name: 'master', cron: cron_daily],
]

def other_cron_jobs = [
  [name: 'conjur-cookbook', cron: cron_weekly]
]

// Create a __cron folder to hold jobs
def folderName = '__cron'
folder(folderName) {
  description('cron jobs, run periodically')
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
    def j = job("${folderName}/${cron_job.name}-cron") {
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

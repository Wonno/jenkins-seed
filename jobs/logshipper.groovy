def repoUrl = 'git@github.com:conjurinc/logshipper.git'
def projectName = 'logshipper'
def platforms = ['el6', 'el7', 'precise', 'trusty']

def platformJobs = platforms.collect{"${projectName}_${it}"}

use(conjur.Conventions) {
  def mainJob = job(projectName) {
    description('Builds and tests logshipper packages'.stripIndent())
    steps {
      downstreamParameterized {
        trigger(platformJobs.join(',')) {
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

  mainJob.applyCommonConfig()
  mainJob.addGitRepo(repoUrl)

  platforms.each { platform ->
    def jobName = "${projectName}_${platform}"
    def job = job(jobName) {
      description("Builds and tests logshipper packages for ${platform}".stripIndent())
      steps {
        shell("./jenkins.sh ${platform}")
      }
      publishers {
        archiveJunit('reports/*/*')
        archiveArtifacts('pkg/*')
      }
    }
    job.applyCommonConfig()
    job.addGitRepo(repoUrl, false)
  }
}

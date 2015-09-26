package utilities

import javaposse.jobdsl.dsl.Job

class Utilities {
  static Job createStandardJob(def jobFactory, def name, def _description, def repoName) {
    return jobFactory.job(name) {
      label('docker && slave')
      description(_description)
      logRotator(-1, 20, -1, 20)

      parameters {
        stringParam('BRANCH', '', 'Git branch or SHA of the appliance repo to build. Not required.')
      }

      scm {
        git {
          remote {
            url(repoName)
          }
          branch('$BRANCH')
        }
      }

      triggers {
        githubPush()
      }

      wrappers {
        preBuildCleanup()
        colorizeOutput()
        buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
      }

      steps {
        shell('./jenkins.sh')
      }

      publishers {
        slackNotifications {
          projectChannel('#jenkins')
          notifyFailure()
          notifyUnstable()
          notifyBackToNormal()
        }
      }
    }
  }

  static void addManualPromotion(def job, def _name, def triggeredJob, def propName, def propVal) {
    job.with {
      properties {
        promotions {
          promotion {
            name(_name)
            icon("star-gold")
            conditions {
              manual('')
            }
            actions {
              downstreamParameterized {
                trigger(triggeredJob) {
                  condition('SUCCESS')
                  block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                  }
                  parameters {
                    predefinedProp(propName, propVal)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

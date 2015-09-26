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
}

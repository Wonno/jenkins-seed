package utilities

class Conjur {
  static void createStandardJob(def jobFactory, def jobName, def jobDescription, def jobRepo) {
    jobFactory.job(jobName) {
      label('docker && slave')
      description(jobDescription)
      logRotator(30, -1, -1, 30)

      scm {
        git(jobRepo)
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
        archiveJunit('spec/reports/*.xml,features/reports/*.xml,integration_features/reports/*.xml')
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

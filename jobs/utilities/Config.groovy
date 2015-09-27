package utilities

class Config {
  static void applyCommonConfig(def job) {
    job.with {
      label('docker && slave')
      logRotator(-1, 20, -1, 20)

      wrappers {
        preBuildCleanup()
        colorizeOutput()
        buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
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

  static void addGitRepo(def job, def repoUrl, def triggerOnPush=true) {
    job.with {
      parameters {
        stringParam('BRANCH', '', 'Git branch or SHA to build. Not required.')
      }

      scm {
        git {
          remote {
            url(repoUrl)
          }
          branch('$BRANCH')
        }
      }

      if (triggerOnPush) {
        triggers {
          githubPush()
        }
      }
    }
  }
}

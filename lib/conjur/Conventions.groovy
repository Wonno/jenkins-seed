import javaposse.jobdsl.dsl.Job

class Conventions {
  // Applies common configuration to a job
  static void applyCommonConfig(Job job) {
    job.with {
      label('docker && slave')
      logRotator(-1, 30, -1, 30)

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

  // Listens for Github pushes, defines a BRANCH param for overriding
  static void addGitRepo(Job job, String repoUrl, boolean triggerOnPush=true) {
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

  // Overrides the build name, shown in Job page left column
  static void setBuildName(Job job, String name) {
    job.with {
      wrappers {
        buildName(name)
      }
    }
  }
}
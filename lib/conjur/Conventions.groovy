package conjur

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
        timestamps()
        timeout {
            likelyStuck()
        }
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
  static void addGitRepo(Job job, String repoUrl, boolean triggerOnPush=true, String listenOn='') {
    job.with {
      parameters {
        stringParam('BRANCH', listenOn, 'Git branch or SHA to build. Not required.')
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

  static void publishDebianOnSuccess(Job job) {
    job.with {
      configure { project ->
        project / properties << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
          projectNameList {
            string 'release_debian'
          }
        }
      }
      steps {
        downstreamParameterized {
          trigger('release_debian') {
            condition('SUCCESS')
            block {
              buildStepFailure('FAILURE')
              failure('FAILURE')
              unstable('UNSTABLE')
            }
            parameters {
              predefinedProp('PROJECT_NAME', '$JOB_NAME')
              predefinedProp('BUILD_NUMBER', '$BUILD_NUMBER')
              predefinedProp('GIT_BRANCH', '$GIT_BRANCH')
            }
          }
        }
      }
    }
  }

  // Publish build artifacts to Artifactory
  static void publishToArtifactory(Job job, String targetRepo, String artifactRegex, String deploymentProperties) {
    job.with {
      configure { project ->
        project / buildWrappers << 'org.jfrog.hudson.generic.ArtifactoryGenericConfigurator' {
          details {
            artifactoryName('-1280243840@1442969665256')
            artifactoryUrl('https://conjurinc.artifactoryonline.com/conjurinc')
            deployReleaseRepository {
              keyFromSelect(targetRepo)
              dynamicMode(false)
            }
          }
          deployPattern(artifactRegex)
          matrixParams(deploymentProperties)
          deployBuildInfo(true)
          includeEnvVars(false)
          discardOldBuilds(false)
          discardBuildArtifacts(false)
        }
      }
    }
  }
}
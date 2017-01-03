package conjur

import javaposse.jobdsl.dsl.Job

class Conventions {
  static {
    Map.metaClass.fetch = { key, defaultValue ->
        delegate.containsKey(key) ? delegate[key] : defaultValue
    }
  }

  // Applies common configuration to a job
  static void applyCommonConfig(Job job, Map args=[:]) {
    def cleanup = args.fetch('cleanup', true)
    def notifyOnRepeatedFailure = args.fetch('notifyRepeatedFailure', false)

    job.with {
      label('executor')
      logRotator(-1, 30, -1, 30)

      concurrentBuild()
      throttleConcurrentBuilds {
        maxPerNode(1)
      }

      wrappers {
        // note: necessary because of broken permissions from the
        // docker build process; remove after fixing that
        if (cleanup) {
          preBuildCleanup()
        }

        colorizeOutput()
        timestamps()
        buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
      }

      publishers {
        slackNotifier {
          room('jenkins')
          notifyFailure(true)
          notifyRepeatedFailure(notifyOnRepeatedFailure)
          notifyUnstable(true)
          notifyBackToNormal(true)
          commitInfoChoice('AUTHORS_AND_TITLES')
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
          extensions {
            cleanBeforeCheckout()
          }
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
      publishers {
        postBuildScripts {
          onlyIfBuildSucceeds()
          steps {
            shell('''
            cat << YML > secrets.yml
            ARTIFACTORY_USERNAME: !var artifactory/users/jenkins/username
            ARTIFACTORY_PASSWORD: !var artifactory/users/jenkins/password
            YML
            '''.stripIndent())

            shell('''
            #!/bin/bash -e

            COMPONENT="testing"

            if [ "$GIT_BRANCH" == "origin/master" ]; then
              COMPONENT="stable"
            fi

            rm -f *latest*.deb
            summon debify publish -c $COMPONENT *.deb
            '''.stripIndent())
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

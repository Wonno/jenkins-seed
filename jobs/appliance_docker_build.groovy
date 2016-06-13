use(conjur.Conventions) {
  def job = job('appliance-docker-build') {
    description('''
      Starter job for the
      <a href="/view/Docker%20Appliance%20Pipeline/">
        Conjur Docker appliance pipeline
      </a>

      <hr>
      <strong>Promotion</strong>
        <ul>
          <li>
            Autopromotion happens when downstream tests pass.
            <br>This is signified by a blue star.
          </li>
          <li>
            To create an AMI, click on a build with a blue star, go to Promotion Status, and Approve "Release AMI".
            <br>This is signified by a green star.
          </li>
      </ul>
    '''.stripIndent())

    concurrentBuild()
    throttleConcurrentBuilds {
      maxPerNode(1)
      maxTotal(2)
    }

    wrappers {
      rvm('2.1.5@appliance-docker-build')
      preBuildCleanup()
    }
    steps {
      shell('./jenkins.sh')
      environmentVariables {
        propertiesFile('env.properties')
      }
      downstreamParameterized {
        trigger('appliance-docker-api-acceptance, appliance-docker-ha-acceptance') {
          block {
            buildStepFailure('UNSTABLE')
            failure('UNSTABLE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            gitRevision()
            propertiesFile('env.properties')
          }
        }
      }

      downstreamParameterized {
        trigger('docker_tag_and_push') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            sameNode()
            propertiesFile('env.properties')
            predefinedProp('IMAGE_NAME', 'registry.tld/conjur-appliance')
          }
        }
      }
      shell('./tag_and_push_stable.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('ci/output/*')
        pattern('TAG=*')
        pattern('env.properties')
      }
    }

    configure { project ->
      project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
        projectNameList {
          string 'appliance-docker-build'
        }
      }
    }

    properties {
      promotions {
        promotion {
          name('Release AMI')
          icon('star-green')
          conditions {
            manual('')
          }
          actions {
            copyArtifacts('$PROMOTED_JOB_NAME') {
              includePatterns('env.properties')
              buildSelector {
                buildNumber('$PROMOTED_NUMBER')
              }
            }
            downstreamParameterized {
              trigger('appliance-docker-ami') {
                parameters {
                  propertiesFile('env.properties')
                }
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/appliance.git')
}

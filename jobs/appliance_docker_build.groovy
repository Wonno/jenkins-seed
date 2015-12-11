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

    wrappers {
      rvm('2.1.5@appliance-docker-build')
    }

    steps {
      copyArtifacts('evoke') {
        includePatterns('conjur-evoke_latest_amd64.deb')
        targetDirectory('.')
        buildSelector() {
          latestSuccessful(true)
        }
      }
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

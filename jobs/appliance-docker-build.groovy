import utilities.Config

def job = job('appliance-docker-build') {
  description('Build the Conjur Docker container')
  concurrentBuild()

  wrappers {
    rvm('2.1.5@appliance-docker-build')
  }

  steps {
    shell('''
      #!/bin/bash -e
      bundle install
      ./ci/bin/jenkins-docker-build $CONJUR_DOCKER_REGISTRY $BUILD_TAG
    '''.stripIndent())
  }

  publishers {
    archiveArtifacts('ci/output/*')
    downstreamParameterized {
      trigger('appliance-docker-api-acceptance, appliance-docker-ha-acceptance') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          gitRevision()
        }
      }
    }
  }

  def testJobs = ['appliance-docker-api-acceptance', 'appliance-docker-ha-acceptance']

  properties {
    promotions {
      testJobs.each { -> testJob
        promotion {
          name("PASSED ${testJob}")
          icon("star-green")
          conditions {
            downstream(false, testJobs.join(','))
          }
        }
      }
    }
  }
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git')
Config.applyCommonConfig(job)

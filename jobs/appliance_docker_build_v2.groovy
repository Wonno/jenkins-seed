use(conjur.Conventions) {
  def job = job('appliance-docker-build-v2') {
    description('''
      Starter job for the
      <a href="/view/Docker%20Appliance%20Pipeline/">
        Conjur Docker appliance pipeline
      </a>
    '''.stripIndent())

    wrappers {
      preBuildCleanup()
    }

    parameters {
      stringParam('TAG', '', 'Override the version tag')
    }

    steps {
      shell('./jenkins.sh')

      downstreamParameterized {
        trigger('appliance-docker-api-acceptance-v2') {
          block {
            buildStepFailure('UNSTABLE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            gitRevision()
            propertiesFile('env.properties')
          }
        }
      }
      shell('echo "Push stable images"')
    }

    publishers {
      archiveArtifacts {
        pattern('*.deb,env.properties,TAG,IMAGE')
      }
    }
  }

  job.applyCommonConfig(label: 'executor-v2')
  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
}

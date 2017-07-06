// Build flow runs the following jobs in parallel
use(conjur.Conventions) {
  def flowJob = job('cli-ruby-omnibus') {
    description('Builds deb, rpm and pkg packages for the Conjur CLI.')

    parameters {
      stringParam('BUILD_VERSION', 'LATEST', 'Version of the CLI to build')
    }

    triggers {
      cron('@weekly')
    }

    steps {
      downstreamParameterized {
        trigger('cli-ruby-ubuntu, cli-ruby-centos, cli-ruby-osx') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            predefinedProp('BUILD_VERSION', params['BUILD_VERSION'])
          }
        }
      }
    }
  }
  flowJob.applyCommonConfig()

  // centos and ubuntu jobs
  ['centos', 'ubuntu'].each { platform ->
    def j = job("cli-ruby-${platform}") {
      description("Builds the Conjur CLI package for ${platform}")

      parameters {
        stringParam('BUILD_VERSION', 'LATEST', 'Version of the CLI to build')
      }

      steps {
        shell("""
          if [ \"\${BUILD_VERSION}\" == \"LATEST\" ]; then
            export BUILD_VERSION=\$(gem query -r -n conjur-cli | grep -oE \"[0-9]{1,2}.[0-9]{1,2}.[0-9]{1,2}\")
          fi

          make VERSION=\${BUILD_VERSION}-1 test-${platform}
        """.stripIndent())
      }

      publishers {
        archiveArtifacts('pkg/*')
      }
    }
    j.applyCommonConfig()
    j.addGitRepo('git@github.com:conjurinc/omnibus-conjur.git', false)
  }

  // OSX job is a little different
  def osxJob = job('cli-ruby-osx') {
    description('Builds the Conjur CLI package for osx')
    disabled()

    parameters {
      stringParam('BUILD_VERSION', 'LATEST', 'Version of the CLI to build')
    }

    steps {
      shell('sudo rm -rf /opt/conjur/*')
      shell('''
        gem install bundler
        bundle install --binstubs
      '''.stripIndent())
      shell('''
        if [ \"\${BUILD_VERSION}\" == \"LATEST\" ]; then
          export BUILD_VERSION=\$(gem query -r -n conjur-cli | grep -oE \"[0-9]{1,2}.[0-9]{1,2}.[0-9]{1,2}\")
        fi
        security unlock-keychain -p "10693930"
        bundle exec bin/omnibus build conjur
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('pkg/*')
    }
  }
  osxJob.applyCommonConfig()
  osxJob.with {
    scm {
      git {
        remote {
          url('git@github.com:conjurinc/omnibus-conjur.git')
          credentials('macstadium')
        }
      }
    }
    label('osx && slave')
  }
}

def artifacts = '*.deb, *=*, *.properties, Gemfile*'

use(conjur.Conventions) {
  def job = job('cli-ruby-deb') {
    description('Builds a non-Omnibus deb for the Ruby CLI')

    parameters {
      stringParam('DISTRIBUTION', 'conjurtools', 'apt distribution to push package to')
    }

    wrappers {
      rvm('2.0.0@cli-ruby-deb')
    }

    steps {
      shell('./build-deb.sh')
      shell('''
        VERSION=$(git describe --long --tags --abbrev=7 | sed -e 's/^v//')
        echo "DISTRIBUTION=\$DISTRIBUTION" > env.properties
        echo "VERSION=\$VERSION" >> env.properties
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, acceptance-features/reports/*.xml')
      archiveArtifacts(artifacts)
    }

    properties {
      promotions {
        promotion {
          name("Publish to apt stable")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            copyArtifacts('$PROMOTED_JOB_NAME') {
              includePatterns(artifacts)
              buildSelector {
                buildNumber('$PROMOTED_NUMBER')
              }
            }
            environmentVariables {
              propertiesFile('env.properties')
            }
            shell('debify publish --component stable $DISTRIBUTION cli')
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

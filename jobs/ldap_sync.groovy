def artifacts = '*.deb, *=*, *.properties, Gemfile*'

use(conjur.Conventions) {
  def job = job('ldap-sync') {
    description('Build and test ldap-sync')

    parameters {
      stringParam('DISTRIBUTION', 'conjurtools', 'apt distribution to push package to')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
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
            shell('debify publish --component stable $DISTRIBUTION ldap-sync')
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/ldap-sync.git')
}

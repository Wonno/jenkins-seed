def services = [
  'audit',
  'authn',
  'authn-ldap',
  'authn-local',
  'authn-tv',
  'authz',
  'core',
  'expiration',
  'evoke',
  'host-factory',
  'ldap-server',
  'policy-loader',
  'pubkeys'
]

use(conjur.Conventions) {
  services.each { service ->
    def serviceJob = job(service) {
      description("""
        <p>Builds packages and tests ${service}.</p>

        <p>
          Promote to 'stable' apt component by
          approving the 'Publish to apt stable' promotion.
        </p>
        <p>Created by 'appliance_services.groovy'</p>
      """.stripIndent())

      if (service == 'authz') {
        wrappers {
          rvm('2.0.0@conjur-authz')
        }
      }

      steps {
        if (service == 'authz') {
          shell('''
            gem install -N bundler
            bundle install --without "production appliance"
          '''.stripIndent())
        }
        shell('./jenkins.sh')
        shell('''
          #!/bin/bash -ex

          export DEBUG=true
          export GLI_DEBUG=true

          DISTRIBUTION=$(cat VERSION_APPLIANCE)
          COMPONENT=$(echo \${GIT_BRANCH#origin/} | tr '/' '.')

          echo "Publishing $JOB_NAME to distribution '$DISTRIBUTION', component '$COMPONENT'"

          debify publish --component $COMPONENT $DISTRIBUTION $JOB_NAME

          touch "DISTRIBUTION=\$DISTRIBUTION"
          touch "COMPONENT=\$COMPONENT"
          echo -n "DISTRIBUTION=\$DISTRIBUTION" > env.properties
        '''.stripIndent())
      }

      publishers {
        archiveArtifacts('*.deb, DISTRIBUTION=*, COMPONENT=*, *.properties')
        archiveJunit('spec/reports/*.xml, features/reports/*.xml, reports/*.xml')
      }

      configure { project ->
        project / properties << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
          projectNameList {
            string "${service}"
          }
        }
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
                includePatterns('env.properties')
                buildSelector {
                  buildNumber('$PROMOTED_NUMBER')
                }
              }
              environmentVariables {
                propertiesFile('env.properties')
              }
              shell('debify publish --component stable $DISTRIBUTION $PROMOTED_JOB_NAME')
            }
          }
        }
      }
    }
    serviceJob.applyCommonConfig()
    serviceJob.addGitRepo("git@github.com:conjurinc/${service}.git")
  }
}
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
      description("Builds packages and tests ${service}. Created by 'appliance_services.groovy'")

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
          COMPONENT=$(echo $GIT_BRANCH | sed 's/^origin\\///' | tr '/' '.')

          echo "Publishing $JOB_NAME to distribution '$DISTRIBUTION', component '$COMPONENT'"

          debify publish --component $COMPONENT $DISTRIBUTION $JOB_NAME

          touch "DISTRIBUTION=\$DISTRIBUTION"
          touch "COMPONENT=\$COMPONENT"
        '''.stripIndent())
      }

      publishers {
        archiveArtifacts('*.deb, DISTRIBUTION=*, COMPONENT=*')
        archiveJunit('spec/reports/*.xml, features/reports/*.xml, reports/*.xml')
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
              shell('''
                DISTRIBUTION=$(cat VERSION_APPLIANCE)
                debify publish --component stable $DISTRIBUTION $PROMOTED_JOB_NAME
              '''.stripIndent())
            }
          }
        }
      }
    }
    serviceJob.applyCommonConfig()
    serviceJob.addGitRepo("git@github.com:conjurinc/${service}.git")
  }
}
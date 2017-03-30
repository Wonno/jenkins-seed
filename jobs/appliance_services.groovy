import conjur.Appliance

def artifacts = '*.deb, *=*'

def services_to_migrate = [
  'glider', 'ldap-sync', 'policy-loader', 'possum'
]

use(conjur.Conventions) {
  Appliance.getServices().each { service ->
    def serviceJob = job(service) {
      description("""
        <p>Builds packages and tests ${service}.</p>
        <p>Created by 'appliance_services.groovy'</p>
      """.stripIndent())

      steps {
        shell('./jenkins.sh')
      }

      publishers {
        archiveJunit('spec/reports/*.xml, features/reports/**/*.xml, scaling_features/reports/**/*.xml, reports/*.xml')
        postBuildScripts {
          steps {
            shell('''
              #!/bin/bash -ex

              export DEBUG=true
              export GLI_DEBUG=true

              DISTRIBUTION=$(cat VERSION_APPLIANCE)
              COMPONENT=$(echo \${GIT_BRANCH#origin/} | tr '/' '.')

              if [ "$COMPONENT" == "master" ] || [ "$COMPONENT" == "v$DISTRIBUTION" ]; then
                COMPONENT=stable
              fi

              echo "Publishing $JOB_NAME to distribution '$DISTRIBUTION', component '$COMPONENT'"

              debify publish --component $COMPONENT $DISTRIBUTION $JOB_NAME

              if [ -f VERSION ]; then
                VERSION="$(debify detect-version | tail -n 1)"
              else
                VERSION=$(git describe --long --tags --abbrev=7 --match 'v*.*.*' | sed -e 's/^v//')
              fi

              touch "DISTRIBUTION=\$DISTRIBUTION"
              touch "COMPONENT=\$COMPONENT"
              touch "VERSION=\$VERSION"
            '''.stripIndent())
          }
          onlyIfBuildSucceeds(true)
        }
        archiveArtifacts(artifacts)
      }

      configure { project ->
        project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
          projectNameList {
            string "${service}"
          }
        }
      }
    }
    serviceJob.applyCommonConfig(label: (service in services_to_migrate) ? 'executor' : 'executor-v2')
    serviceJob.addGitRepo("git@github.com:conjurinc/${service}.git")
  }
}

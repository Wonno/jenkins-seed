import conjur.Appliance

def artifacts = '*.deb, *=*'

def services_to_migrate = ['ldap-sync']

use(conjur.Conventions) {
  Appliance.getServices().each { service ->
    def serviceJob = job(service) {
      description("""
        <p>Builds packages and tests ${service}.</p>
        <p>Created by 'appliance_services.groovy'</p>
      """.stripIndent())

      steps {
        shell('./jenkins.sh')
        shell('debify publish --version $TAG "$(cat VERSION_APPLIANCE)" appliance')
      }

      publishers {
        archiveJunit('spec/reports/*.xml, features/reports/**/*.xml, scaling_features/reports/**/*.xml, reports/*.xml')
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

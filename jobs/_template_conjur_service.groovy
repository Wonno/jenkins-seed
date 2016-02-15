def templatesFolder = 'templates'

folder(templatesFolder)

use(conjur.Conventions) {
  def templateJob = job("${templatesFolder}/conjur_service") {
    description("Template job for Conjur services")
    steps {
      shell('./jenkins.sh')
      shell('''
        #!/bin/bash -ex

        # Publishes a Debian package via debify

        export DEBUG=true
        export GLI_DEBUG=true

        PACKAGE_NAME=$JOB_NAME
        DISTRIBUTION=$(cat VERSION_APPLIANCE)
        COMPONENT=$(echo $GIT_BRANCH | sed 's/^origin\\///' | tr '/' '.')

        echo "Publishing $PACKAGE_NAME to distribution '$DISTRIBUTION', component '$COMPONENT'"

        debify publish \
        --component $COMPONENT \
        $DISTRIBUTION \
        $PACKAGE_NAME

        touch "DISTRIBUTION=\$DISTRIBUTION"
        touch "COMPONENT=\$COMPONENT"
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, reports/*.xml')
    }
  }
  templateJob.applyCommonConfig()
}

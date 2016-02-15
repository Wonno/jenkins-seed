def templatesFolder = 'templates'

folder(templatesFolder)

use(conjur.Conventions) {
  def templateJob = job("${templatesFolder}/conjur_service") {
    description("Template job for Conjur services")
    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, reports/*.xml')
    }
  }
  templateJob.applyCommonConfig()
}

use(conjur.Conventions) {
  def job = job('jenkins-conjur-plugin') {
    description('Build the Conjur Jenkins Plugin and deploy it to Artifactory.')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/jenkins-conjur-plugin.git')
}
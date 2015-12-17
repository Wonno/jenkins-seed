use(conjur.Conventions) {
  def job = job('appliance-docker-build-debian') {
    description('Builds the conjur_appliance debian wrapper package')

    steps {
      shell('make clean package')
    }

    publishers {
      archiveArtifacts('*.deb')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
}
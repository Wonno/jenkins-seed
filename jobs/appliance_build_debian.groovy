use(conjur.Conventions) {
  def job = job('appliance-build-debian') {
    description('Builds the conjur-appliance debian wrapper package')

    steps {
      shell('make clean publish')
    }

    publishers {
      archiveArtifacts('*.deb')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
}
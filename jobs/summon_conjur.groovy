use(conjur.Conventions) {
  def repoName = 'summon-conjur'
  def job = job(repoName) {
    description('Build and test the summon-conjur provider')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('junit.xml')
      archiveArtifacts {
        pattern('pkg/**/*')
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo("git@github.com:conjurinc/${repoName}.git")
}

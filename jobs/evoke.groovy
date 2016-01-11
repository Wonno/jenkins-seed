use(conjur.Conventions) {
  def job = job('evoke') {
    description('Build and test evoke, the configuration and server management tool.')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
      downstreamParameterized {
        trigger('release_debian') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision()
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/evoke.git')
}

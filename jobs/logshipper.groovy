use(conjur.Conventions) {
  def job = matrixJob('logshipper') {
    description('Build and test logshipper for several platforms')

    axes {
      label('label', 'executor') // Restrict to run child jobs on slaves tagged 'executor'
      text('PLATFORM', 'el6', 'el7', 'precise', 'trusty', 'jessie')
    }

    steps {
      shell('./jenkins.sh ${PLATFORM}')
    }

    publishers {
      archiveArtifacts('pkg/*')
      archiveJunit('reports/*/*')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/logshipper.git')
}

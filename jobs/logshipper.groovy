use(conjur.Conventions) {
  def job = matrixJob('logshipper') {
    description('Build and test logshipper for several platforms')

    axes {
      label('label', 'docker') // Restrict to run child jobs on slaves tagged 'docker'
      text('PLATFORM', 'el6', 'el7', 'precise', 'trusty')
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

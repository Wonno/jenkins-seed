use(conjur.Conventions) {
  def job = matrixJob('logshipper') {
    description('Build and test logshipper for several platforms')

    axes {
      label('label', 'executor') // Restrict to run child jobs on slaves tagged 'executor'
      text('PLATFORM', 'el6', 'el7', 'precise', 'trusty', 'xenial', 'jessie', 'stretch')
    }

    steps {
      shell('./jenkins.sh ${PLATFORM}')
    }

    publishers {
      archiveArtifacts('pkg/*')
      archiveJunit('reports/*/*')
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.addGitRepo('git@github.com:conjurinc/logshipper.git')
}

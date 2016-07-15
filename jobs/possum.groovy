use(conjur.Conventions) {
  def job = job('possum') {
    description('Test Possum')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }

    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
    }
  }

  job.addGitRepo('git@github.com:conjurinc/possum.git')
  job.applyCommonConfig()
}

use(conjur.Conventions) {
  def job = job('possum') {
    description('Test Possum')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, cucumber/api/features/reports/*.xml, cucumber/policy/features/reports/*.xml')

      postBuildScripts {
        steps {
          shell('./publish.sh')
        }
        onlyIfBuildSucceeds(true)
      }
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

use(conjur.Conventions) {
  def job = job('conjur-ui') {
    description('Build new Conjur UI')

    concurrentBuild()
    throttleConcurrentBuilds {
      categories(['resource-intensive'])
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('*.deb')
        archiveJunit('features/reports/*.xml, spec/reports/*.xml, client/reports/*.xml')
      }
    }
  }
  job.applyCommonConfig(concurrent: false)
  job.addGitRepo("git@github.com:conjurinc/conjur-ui.git")
  job.publishDebsOnSuccess('ui')  // debify prepends 'conjur-'
}

use(conjur.Conventions) {
  def job = job('possum-example') {
    description('Builds the possum-example project')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      postBuildScripts {
        steps {
          shell('./publish.sh')
        }
        onlyIfBuildSucceeds(true)
      }
    }
    
    properties {
      promotions {
        promotion {
          name("Publish the version indicated in the VERSION file")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            shell('./publish.sh -v stable')
            shell('./publish.sh -v $(cat VERSION)')
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/possum-example.git')
}

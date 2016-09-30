use(conjur.Conventions) {
  def job = job('debify') {
    description('conjur-debify - build and test deb packages for the Conjur appliance')

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.0.0@debify && export > rvm.env"')
      shell('source rvm.env && ./jenkins.sh')
    }

    publishers {
      archiveJunit('features/reports/*.xml')
    }

    properties {
      promotions {
        promotion {
          name("Release to Rubygems")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME","conjur-debify")
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/debify.git')
  job.applyCommonConfig()
}

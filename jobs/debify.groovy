use(conjur.Conventions) {
  def job = job('debify') {
    description('conjur-debify - build and test deb packages for the Conjur appliance')

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.2.6@debify && export > rvm.env"')
      shell('source rvm.env && ./jenkins.sh')
    }

    publishers {
      archiveJunit('features/reports/*.xml')
    }

    properties {
      promotions {
        promotion {
          name('Publish to rubygems')
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger('release-rubygems') {
                block {
                  buildStepFailure('FAILURE')
                  failure('FAILURE')
                  unstable('UNSTABLE')
                }
                parameters {
                  predefinedProp('GEM_NAME', 'conjur-debify')
                }
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

use(conjur.Conventions) {
  def job = job('rack-heartbeat') {
    description('conjur-rack-heartbeat - respond to OPTIONS / with 200 OK')

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.0.0@rack-heartbeat && export > rvm.env"')
      shell('source rvm.env && ./jenkins.sh')
    }

    publishers {
      archiveJunit('report.xml')
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
                  predefinedProp('GEM_NAME', 'conjur-rack-heartbeat')
                }
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/rack-heartbeat.git')
}

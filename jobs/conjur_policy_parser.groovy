use(conjur.Conventions) {
  def job = job('conjur-policy-parser') {
    description('Test the policy parser')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
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
                  predefinedProp('GEM_NAME', 'conjur-policy-parser')
                }
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig(label: 'executor-v2')
  job.addGitRepo('git@github.com:conjurinc/conjur-policy-parser.git')
}

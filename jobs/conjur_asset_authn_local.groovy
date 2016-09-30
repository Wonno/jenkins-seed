use(conjur.Conventions) {
    def job = job('conjur-asset-authn-local') {
        description('Client gem for authn-local')

        steps {
            shell('./jenkins.sh')
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
                      predefinedProp('GEM_NAME', 'conjur-asset-authn-local')
                    }
                  }
                }
              }
            }
          }
        }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjur-asset-authn-local.git')
}

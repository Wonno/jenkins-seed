import utilities.Utilities

def job = Utilities.createStandardJob(
  this,
  'api-ruby',
  'Test the Conjur Ruby client library',
  'git@github.com:conjurinc/api-ruby.git'
)

job.with {
  wrappers {
    rvm('1.9.3@conjur-api')
  }

  steps {
    shell('''
      bundle update
      bundle exec rake jenkins
    '''.stripIndent())
  }

  publishers {
    archiveJunit('spec/reports/*.xml')
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
            trigger('release-rubygems') {
              condition('SUCCESS')
              block {
                buildStepFailure('FAILURE')
                failure('FAILURE')
                unstable('UNSTABLE')
              }
              parameters {
                predefinedProp('GEM_NAME', 'conjur-api')
              }
            }
          }
        }
      }
    }
  }
}

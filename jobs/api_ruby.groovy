use(conjur.Conventions) {
  def job = job('api_ruby') {
    description('Test the Conjur Ruby client library')

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
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME","conjur-api")
              }
            }
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/api-ruby.git')
  job.applyCommonConfig()
}

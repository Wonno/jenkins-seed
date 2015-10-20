use(conjur.Conventions) {
  def job = job('cli-ruby') {
    description('''
      Builds the ruby conjur CLI.
      <hr>
      On promotion, releases the new gem to rubygems and creates packages for different OSes.
      <br>
      <img src="http://i.imgur.com/KON4hh6.png"/>
    '''.stripIndent())

    wrappers {
      rvm('1.9.3@conjur-cli')
    }

    steps {
      shell('''
        bundle update
        bundle exec rake jenkins
        bundle exec rake build
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }

    properties {
      promotions {
        promotion {
          name("Publish to RubyGems")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("GEM_NAME","conjur-cli")
              }
            }
          }
        }
        promotion {
          name("Build Omnibus packages")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("omnibus-conjur", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
              }
            }
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

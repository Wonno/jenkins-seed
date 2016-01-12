use(conjur.Conventions) {
  def job = matrixJob('cli-ruby') {
    description('''
      Builds the ruby conjur CLI.
      <hr>
      On promotion, releases the new gem to rubygems and creates packages for different OSes.
      <br>
      <img src="http://i.imgur.com/KON4hh6.png"/>
    '''.stripIndent())

    axes {
      label('label', 'docker') // Restrict to run child jobs on slaves tagged 'docker'
      text('RUBY', '1.9', '2.0', '2.1', '2.2')
    }

    steps {
      shell('./jenkins.sh $RUBY')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }

    wrappers {
      // note: necessary because of broken permissions from the
      // docker build process; remove after fixing that
      preBuildCleanup()
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

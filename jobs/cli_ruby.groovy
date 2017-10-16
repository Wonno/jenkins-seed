use(conjur.Conventions) {
  def job = matrixJob('cli-ruby') {
    disabled()
    description('''
      Builds the ruby conjur CLI.
      <hr>
      On promotion, releases the new gem to rubygems and creates packages for different OSes.
      <br>
      <img src="http://i.imgur.com/KON4hh6.png"/>
    '''.stripIndent())

    axes {
      label('label', 'executor') // Restrict to run child jobs on slaves tagged 'executor'
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
          name('Publish to Rubygems')
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
                  predefinedProp('GEM_NAME', 'conjur-cli')
                }
              }
            }
          }
        }
        promotion {
          name('Build omnibus packages')
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger('omnibus-conjur') {
                block {
                  buildStepFailure('FAILURE')
                  failure('FAILURE')
                  unstable('UNSTABLE')
                }
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

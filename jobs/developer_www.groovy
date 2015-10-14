use(conjur.Conventions) {
  def job = job('developer-www') {
    description('''
      PURPOSE: Conjur developer web site (developer.conjur.net)
      <br>
      LINKS:
      <a href="https://developer-www-ci-conjur.herokuapp.com/">staging</a>,
      <a href="https://developer.conjur.net">production</a>
      <br>
      PUSH TO PRODUCTION: Promote a build to push to production via release-bot
    '''.stripIndent())

    parameters {
      stringParam('APP_NAME', 'developer-www-ci-conjur', 'Heroku application name')
    }

    steps {
      shell('./test.sh')
      shell('#rake links:check')
    }

    publishers {
      archiveJunit('results/rspec.xml')
      downstreamParameterized {
        trigger('release-heroku') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision()
          }
        }
      }
    }

    properties {
      promotions {
        promotion {
          name("Release to production")
          icon("star-gold")
          conditions {
            manual('')
          }
          actions {
            downstreamParameterized {
              trigger("release-heroku", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                predefinedProp("APP_NAME","developer-www-conjur")
              }
            }
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/developer-www.git')
}
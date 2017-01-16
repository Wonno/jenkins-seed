use(conjur.Conventions) {
    def job = job('release-bot') {
        description('Test and deploy release-bot')

        parameters {
          stringParam('APP_NAME', 'releasebot-conjur', 'Heroku application name')
        }

        steps {
            shell('./jenkins.sh')
        }

        publishers {
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
    }

    job.applyCommonConfig(dailyCron: false)
    job.addGitRepo('git@github.com:conjurinc/release-bot.git')
}

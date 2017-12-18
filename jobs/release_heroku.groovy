use(conjur.Conventions) {
  def job = job('release-heroku') {
    description('Releases a Heroku app using release-bot https://github.com/conjurinc/release-bot')

    parameters {
      stringParam('APP_NAME', 'developer-www-ci-conjur', 'Heroku application name. Must be whitelisted in release-bot!')
    }

    steps {
      shell('''
        set -e

        auth_header=`conjur authn authenticate -H`
        curl -f -H "$auth_header" -X POST "https://releasebot-conjur.herokuapp.com/heroku/releases" --data "name=$APP_NAME"
      '''.stripIndent())
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="APP_NAME"}')
}

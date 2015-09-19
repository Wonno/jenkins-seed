job('release-heroku') {
  description('Releases a Heroku app using release-bot https://github.com/conjurinc/release-bot')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  parameters {
    stringParam('APP_NAME', 'developer-www-ci-conjur', 'Heroku application name. Must be whitelisted in release-bot!')
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${ENV,var="APP_NAME"}')
  }

  steps {
    shell('''
      set -e

      auth_header=`/opt/conjur/bin/conjur authn authenticate -H`
      curl -f -H "$auth_header" -X POST "https://releasebot-conjur.herokuapp.com/heroku/releases" --data "name=$APP_NAME"
    '''.stripIndent())
  }
}
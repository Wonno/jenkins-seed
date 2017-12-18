use(conjur.Conventions) {
  def job = job('release-npm') {
    description('Releases a NPM package using release-bot https://github.com/conjurinc/release-bot')

    parameters {
      stringParam('PACKAGE_NAME', '', 'NPM Package Name. Must be whitelisted in release-bot!')
    }

    steps {
      shell('''
        set -e

        auth_header=`conjur authn authenticate -H`
        curl -f -H "$auth_header" -X POST "https://releasebot-conjur.herokuapp.com/npm/releases" --data "name=$PACKAGE_NAME"
      '''.stripIndent())
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="PACKAGE_NAME"}')
}

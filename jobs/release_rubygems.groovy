use(conjur.Conventions) {
  def job = job('release-rubygems') {
    description('Releases a rubygems using release-bot https://github.com/conjurinc/release-bot')

    parameters {
      stringParam('GEM_NAME', '', 'Ruby Gem Name. Must be whitelisted in release-bot!')
    }

    steps {
      shell('''
        set -e

        auth_header=`/opt/conjur/bin/conjur authn authenticate -H`
        curl -f -H "$auth_header" -X POST "https://releasebot-conjur.herokuapp.com/rubygems/releases" --data "name=$GEM_NAME"
      '''.stripIndent())
    }
  }
  job.applyCommonConfig(dailyCron: false)
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="GEM_NAME"}')
}

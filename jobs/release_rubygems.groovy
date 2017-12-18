use(conjur.Conventions) {
  def job = job('release-rubygems') {
    description('Releases a rubygems using release-bot https://github.com/conjurinc/release-bot')

    parameters {
      stringParam('GEM_NAME', '', 'Ruby Gem Name. Must be whitelisted in release-bot!')
      stringParam('GEM_BRANCH', '', 'Git branch to release. Default is master')
    }

    steps {
      shell('''
        set -e

        data="name=$GEM_NAME"
        if [[ "$GEM_BRANCH" != "" ]]; then
          data="$data&branch=$GEM_BRANCH"
        fi

        auth_header=`conjur authn authenticate -H`
        curl -f -H "$auth_header" -X POST "https://releasebot-conjur.herokuapp.com/rubygems/releases" --data "$data"
      '''.stripIndent())
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="GEM_NAME"}')
}

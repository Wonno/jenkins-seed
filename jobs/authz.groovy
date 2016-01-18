use(conjur.Conventions) {
  def job = job('authz') {
    description('Test the Conjur authz core service')

    wrappers {
      rvm('2.0.0@conjur-authz')
    }

    steps {
      shell('''
        set -e
        gem install -N bundler
        bundle install --without "production appliance"
        ./jenkins.sh
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
      postBuildScripts {
          steps {
              shell('./publish.sh')
          }
          onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authz.git')
}

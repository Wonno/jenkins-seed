use(conjur.Conventions) {
  def job = job('core') {
    description('Test the Conjur core ...core service')

    wrappers {
      rvm('2.0.0@conjur-core')
    }

    steps {
      shell('''
        bundle install
        SKIP_AUDIT_TESTS=yes DATABASE_SCHEMA=core-ci
        CONJURRC=/var/lib/jenkins/conjurops/.conjurrc \
        bundle exec conjur env:run \
        --yaml "{ conjur_admin_password: !var build-0.1.0/conjur-ci/admin-password , conjurrc:  /var/lib/jenkins/.conjurrc , conjur_account: ci, conjurapi_log: stderr, restclient_log: stderr }" \
        -- bundle exec rake jenkins
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/core.git')
}

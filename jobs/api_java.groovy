use(conjur.Conventions) {
  def job = mavenJob('api_java') {
    description('Test the Conjur Java client library')

    preBuildSteps {
      shell('''
        export GEM_PATH=/opt/conjur/embedded/lib/ruby/gems/1.9.1/
        export CONJURRC=/var/lib/jenkins/conjurops/.conjurrc
        export CONJUR_ADMIN_PASSWORD=$(/opt/conjur/bin/conjur env:run --yaml "conjur_admin_password: !var build-0.1.0/conjur-ci/admin-password" -- printenv CONJUR_ADMIN_PASSWORD)
        unset CONJURRC

        export CONJUR_CREDENTIALS="admin:$CONJUR_ADMIN_PASSWORD"
        export CONJUR_ACCOUNT=ci
        export CONJUR_STACK=ci
      '''.stripIndent())
    }

    goals('clean package')

    publishers {
      archiveJunit('report/xunit.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/api-java.git')
}

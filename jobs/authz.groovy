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
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/authz.git')
}

// Can't use this yet, job is still RVM-ified - DC 1/27/2016
//use(conjur.Conventions) {
//  def job = job('authz') {
//    using('templates/conjur_service')
//    description('Test the Conjur authz core service')
//  }
//  job.addGitRepo('git@github.com:conjurinc/authz.git')
//}


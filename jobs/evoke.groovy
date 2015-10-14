use(conjur.Conventions) {
  def job = job('evoke') {
    description('Test evoke, Conjur configuration and server management tool.')

    wrappers {
      rvm('2.0.0@evoke')
    }

    steps {
      shell('''
        gem install -N bundler
        bundle install
        bundle exec rspec
      '''.stripIndent())
    }

    publishers {
      archiveJunit('spec-results.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/evoke.git')
}

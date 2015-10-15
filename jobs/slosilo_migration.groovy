use(conjur.Conventions) {
  def job = job('slosilo-migration') {
    description('Test slosilo-migration.')

    wrappers {
      rvm('2.0.0@slosilo-migration')
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
  job.addGitRepo('git@github.com:conjurinc/slosilo-migration.git')
}

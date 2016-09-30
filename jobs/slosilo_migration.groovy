use(conjur.Conventions) {
  def job = job('slosilo-migration') {
    description('Test slosilo-migration.')

    steps {
      shell('''
        bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.0.0@cli-ruby-deb && export > rvm.env"
        source rvm.env

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

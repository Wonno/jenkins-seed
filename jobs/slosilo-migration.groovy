job('slosilo-migration') {
  description('Test slosilo-migration.')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/slosilo-migration.git')
  }

  triggers {
    githubPush()
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    rvm('2.0.0@slosilo-migration')
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
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

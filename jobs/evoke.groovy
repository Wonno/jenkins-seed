job('evoke') {
  description('Test evoke, Conjur configuration and server management tool.')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/evoke.git')
  }

  triggers {
    githubPush()
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    rvm('2.0.0@evoke')
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

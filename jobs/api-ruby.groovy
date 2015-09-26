import utilities.Utilities

def job = Utilities.createStandardJob(
  this,
  'api-ruby',
  'Test the Conjur Ruby client library',
  'git@github.com:conjurinc/api-ruby.git'
)

job.with {
  wrappers {
    rvm('1.9.3@conjur-api')
  }

  steps {
    shell('''
      bundle update
      bundle exec rake jenkins
    '''.stripIndent())
  }

  publishers {
    archiveJunit('spec/reports/*.xml')
  }
}

Utilities.addManualPromotion(
  job, 'Release to Rubygems', 'release-rubygems', 'GEM_NAME', 'conjur-api'
)

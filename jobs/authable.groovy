use(conjur.Conventions) {
  def job = job('authable__v1') {
    description('''
      SCHEDULED BUILD: weekly (to detect regressions against 3rd-party gems)
      <br>
      <dl>
      <dt>DEPS</dt><dd>Gemfile.lock is gitignored, <em>bundle update</em>launched on every build</dd>
      <dd>In Jenkins, github versions of slosilo, conjur-rack, api-ruby are used</dd></dl>
    '''.stripIndent())

    triggers {
      cron('H 12 * * 6 ')
    }

    wrappers {
      rvm('2.0.0@conjur-authable')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.addGitRepo('git@github.com:conjurinc/authable.git')
  job.applyCommonConfig()
}

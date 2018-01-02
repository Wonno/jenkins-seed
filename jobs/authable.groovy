use(conjur.Conventions) {
  def job = job('authable') {
    disabled()

    description('''
      This build is DISABLED until it can be containerized to run on v2 executors.

      SCHEDULED BUILD: weekly (to detect regressions against 3rd-party gems)
      <br>
      <dl>
      <dt>DEPS</dt><dd>Gemfile.lock is gitignored, <em>bundle update</em>launched on every build</dd>
      <dd>In Jenkins, github versions of slosilo, conjur-rack, api-ruby are used</dd></dl>
    '''.stripIndent())

    triggers {
      cron('H 12 * * 6 ')
    }

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.0.0@conjur-authable && export > rvm.env"')
      shell('source rvm.env && ./jenkins.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.addGitRepo('git@github.com:conjurinc/authable.git')
}

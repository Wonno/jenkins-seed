use(conjur.Conventions) {
  def job = job('cli-ruby-deb') {
    description('Builds a non-Omnibus deb for the Ruby CLI')

    wrappers {
      rvm('2.0.0@cli-ruby-deb')
    }

    steps {
      shell('./build-deb.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, acceptance-features/reports/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

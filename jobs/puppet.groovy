use(conjur.Conventions) {
  def job = job('puppet') {
    description('Official Conjur Puppet module')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('rspec.xml')
    }
  }

  job.addGitRepo('git@github.com:conjur/puppet.git')
  job.applyCommonConfig(label: 'executor-v2')
}

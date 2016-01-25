// TODO: 
//   * add publish step (uses ./publish.sh)
//   * add postbuild steps
//     * scan console log, foodcritic parser
//     * report violations (10 999 999 for each 

use(conjur.Conventions) {
  def job = job('conjur-cookbook') {
    description('Lints and tests the <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook')

    wrappers {
      rvm('2.1.5@conjur-cookbook')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveArtifacts('features/reports/*/*.xml, spec/*.xml')
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git')
}

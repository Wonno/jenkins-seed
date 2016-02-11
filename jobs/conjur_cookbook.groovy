// TODO: 
//   * add publish step (uses ./publish.sh)
//   * add postbuild steps
//     * scan console log, foodcritic parser
//     * report violations (10 999 999 for each 

use(conjur.Conventions) {
  def job = job('conjur-cookbook') {
    description('Lints and tests the <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook')

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      archiveJunit('ci/reports/*.xml, spec/*.xml')
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git')
}
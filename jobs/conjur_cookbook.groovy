use(conjur.Conventions) {
  def job = job('conjur-cookbook') {
    description('''
      Lints and tests the <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook.
      Kicks off the "conjur-cookbook-matrix" job to run test-kitchen in parallel.
    '''.stripIndent())

    steps {
      shell('''
        SUITES=$(summon -f secrets.ci.yml kitchen list -b | tr "\n" " ")
        echo "SUITES=$SUITES" >> env.properties
      '''.stripIndent())

      downstreamParameterized {
        trigger('conjur-cookbook-matrix') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            propertiesFile('env.properties')
            currentBuild()
            gitRevision()
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git')
}

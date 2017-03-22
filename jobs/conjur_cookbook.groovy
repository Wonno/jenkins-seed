use(conjur.Conventions) {
  def job = job('conjur-cookbook') {
    description('''
      Lints and tests the <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook.
      Kicks off the "conjur-cookbook-matrix" job to run test-kitchen in parallel.
    '''.stripIndent())

    steps {
      shell('./jenkins.sh')

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

    publishers {
      archiveJunit('ci/reports/specs.xml')
      violations(50) {
        checkstyle(10, 999, 999, 'ci/reports/rubocop.xml')
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git')
}

use(conjur.Conventions) {
  def job = job('conjur-cookbook') {
    description('''
      Lints and tests the <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook.
      Kicks off the "conjur-cookbook-matrix" job to run test-kitchen in parallel.
    '''.stripIndent())

    steps {
      shell('''
        summon -f secrets.ci.yml ./jenkins.sh
      '''.stripIndent())

      //downstreamParameterized {
      //  trigger('conjur-cookbook-matrix') {
      //    block {
      //      buildStepFailure('UNSTABLE')
      //      failure('UNSTABLE')
      //      unstable('UNSTABLE')
      //    }
      //    parameters {
      //      propertiesFile('env.properties')
      //      currentBuild()
      //      gitRevision()
      //    }
      //  }
      //}
    }

    publishers {
      archiveJunit('ci/reports/specs.xml')
      violations(50) {
        checkstyle(10, 999, 999, 'ci/reports/rubocop.xml')
      }
      //postBuildScripts {
      //  steps {
      //    shell('summon -f secrets.ci.yml ./cleanup.sh')
      //  }
      //  onlyIfBuildSucceeds(false)
      //}
      // archiveArtifacts('ci/output/*.tar.gz, env.properties')

    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git')
}

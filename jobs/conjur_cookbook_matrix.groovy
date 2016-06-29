use(conjur.Conventions) {
  def job = matrixJob('conjur-cookbook-matrix') {
    description('''
      Runs test-kitchen suites of the
      <a href="https://github.com/conjur-cookbooks/conjur">conjur</a> cookbook
      in parallel on EC2.
      <br><br>
      Update the SUITES parameter to run a subset of the tests as needed. The names
      are space-delimited and use the same regex as test-kitchen. For example, to run
      all centos and ubuntu suites set SUITES to "centos ubuntu".
    '''.stripIndent())

    parameters {
      stringParam('SUITES', '', 'test-kitchen suites to run')
    }

    axes {
      label('label', 'executor') // Restrict to run child jobs on slaves tagged 'executor'
      configure { axes ->
        axes << 'ca.silvermaplesolutions.jenkins.plugins.daxis.DynamicAxis' {
          name 'SUITE'
          values {
            string 'SUITES'
          }
          varName 'SUITES'
          axisValues {
            string 'default'
          }
        }
      }
    }

    throttleConcurrentBuilds {
        maxPerNode(4)
        maxTotal(4)
    }

    // Per https://issues.jenkins-ci.org/browse/JENKINS-32631, can't
    // throttle matrix configuration jobs directly. Use the workaround
    // suggested in the ticket until it's fixed.
    configure { project ->
      project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' << 'matrixOptions' {
        throttleMatrixBuilds(false)
        throttleMatrixConfigurations(true)
      }
    }

    steps {
      shell('''
        summon -f secrets.ci.yml ./matrix.sh --only ${SUITE}
      '''.stripIndent())
    }

    publishers {
      archiveJunit('ci/reports/*.xml, spec/*.xml')
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjur-cookbooks/conjur.git', false)
}

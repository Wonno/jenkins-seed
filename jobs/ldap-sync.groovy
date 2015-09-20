// Defines ldap-sync build and acceptance jobs
job('ldap-sync-build') {
  description('''
    Builds and pushes ldap-sync to Docker registry

    <a href="https://github.com/conjurinc/ldap-sync/blob/master/README.md">README</a>
  '''.stripIndent())
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/ldap-sync.git')
  }

  triggers {
    githubPush()
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('make build/base build/test build/push')
  }

  publishers {
    downstreamParameterized {
      trigger('ldap-sync-acceptance') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          gitRevision()
          predefinedProp('TEST_TAG', 'conjurinc/acceptance-ldap-sync:build_${BUILD_NUMBER}')
        }
      }
    }
  }

  properties {
    promotions {
      promotion {
        name("Release to production")
        icon("star-green")
        conditions {
          // def downstream(Boolean evenIfUnstable, String jobs)
          downstream(false, 'ldap-sync-acceptance')
        }
      }
    }
  }
}

job('ldap-sync-acceptance') {
  description('Runs acceptance tests against ldap-sync image built by ldap-sync-build')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  parameters {
    stringParam('TEST_TAG', '', 'Tagged appliance image coming from ldap-sync-build')
  }

  scm {
    git('git@github.com:conjurinc/ldap-sync.git')
  }

  steps {
    shell("""
      export PATH=/opt/conjur/bin:$PATH   # somehow it's not done automatically

      conjur env run -c acceptance.conjurenv -- make acceptance/results
    """.stripIndent())
  }

  publishers {
    archiveJunit('test/cukes_report/*/*,test/spec_report/*/*')
    archiveArtifacts('test/*')
  }
}

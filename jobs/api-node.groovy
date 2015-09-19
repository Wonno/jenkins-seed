job('api-node') {
  description('Test the Conjur Node.js client library')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/api-node.git')
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
    shell('./jenkins.sh')
  }

  publishers {
    archiveJunit('report/xunit.xml')
  }

  properties {
    promotions {
      promotion {
        name("Release to NPM")
        icon("star-gold")
        conditions {
          manual('')
        }
        actions {
          downstreamParameterized {
            trigger("release-npm", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
              predefinedProp("PACKAGE_NAME","conjur-api")
            }
          }
        }
      }
    }
  }
}
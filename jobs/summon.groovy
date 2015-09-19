job('summon') {
  description('Build and test the summon binary')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/summon.git')
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
    conditionalSteps {
      condition {
        stringsMatch('${GIT_BRANCH}', 'origin/gh-pages', false)
      }
      runner('Run')
      steps {
        // touch this so pushes to gh-pages branch don't fail the build
        shell('echo \'<?xml version="1.0" encoding="UTF-8"?><testsuites><testsuite tests="1" failures="0" time="0" name="docs"><testcase classname="command" name="dummy" time="0.000"></testcase></testsuite></testsuites>\' > junit.xml')
      }
    }
    conditionalSteps {
      condition {
        not {
          stringsMatch('${GIT_BRANCH}', 'origin/gh-pages', false)
        }
      }
      runner('Run')
      steps {
      shell('./test.sh')
        shell('./build.sh')
        shell('''
          if [ -d "acceptance" ]; then
            cp ${WORKSPACE}/pkg/linux_amd64/summon .
            cd acceptance && make
          fi
        '''.stripIndent())
        shell('sudo chmod -R 777 pkg/ && ./package.sh')
      }
    }

  }

  publishers {
    archiveJunit('junit.xml')
    archiveArtifacts {
      pattern('pkg/**/*')
      allowEmpty()
    }
  }
}
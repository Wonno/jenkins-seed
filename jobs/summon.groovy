use(conjur.Conventions) {
  def job = job('summon') {
    description('Build and test the summon binary')

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
          shell('./jenkins.sh')
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
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/summon.git')
}

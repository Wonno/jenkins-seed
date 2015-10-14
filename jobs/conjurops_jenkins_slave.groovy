use(conjur.Conventions) {
  def buildJob = job('conjurops-jenkins-slave') {
    description('Test the conjurops-jenkins-slave Chef cookbook')

    steps {
      shell('''
        PATH=/opt/conjur/bin:$PATH

        summon -f secrets.ci.yml ./test.sh
      '''.stripIndent())
    }

    publishers {
      downstreamParameterized {
        trigger('conjurops-jenkins-slave-image') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision()
          }
        }
      }
    }
  }
  buildJob.applyCommonConfig()
  buildJob.addGitRepo('git@github.com:conjurinc/conjurops-jenkins-slave.git')

  def imageJob = job('conjurops-jenkins-slave-image') {
    description('Creates an AMI via vagrant after successful build of conjurops-jenkins-slave job.')

    scm {
      git('git@github.com:conjurinc/conjurops-jenkins-slave.git')
    }

    steps {
      shell('''
        export PATH=/opt/conjur/bin:$PATH

        summon -f secrets.ci.yml ./vagrant.sh
      '''.stripIndent())
    }

    publishers {
      archiveArtifacts('*.log')
    }
  }
  imageJob.applyCommonConfig()
  imageJob.addGitRepo('git@github.com:conjurinc/conjurops-jenkins-slave.git', false)
}

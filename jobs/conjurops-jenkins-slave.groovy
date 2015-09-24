job('conjurops-jenkins-slave') {
  description('Test the conjurops-jenkins-slave Chef cookbook')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/conjurops-jenkins-slave.git')
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

job('conjurops-jenkins-slave-image') {
  description('Creates an AMI via vagrant after successful build of conjurops-jenkins-slave job.')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/conjurops-jenkins-slave.git')
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
    
    // Need to pass the credential's UID to sshAgent, rather than the
    // name.  The UID can be found in the Advanced settings for the
    // credentials.
    // sshAgent('jenkins (Read only access to all conjur repos)')
    sshAgent('6ba81099-89ef-4cf6-b438-1511e4002530')
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

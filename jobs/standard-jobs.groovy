jobs = [
  'audit-sqs-migrator' : [
    repo: 'git@github.com:conjurinc/audit-sqs-migrator.git'
  ]
]
jobs.each { jid, config ->
  job(jid) {
    label('docker && slave')
    if ( config.description ) {
    	description(config.description) 
    }
    logRotator(30, -1, -1, 5)
    
    scm {
      git(config.repo)
    }
  
    triggers {
      githubPush()
    }
  
    wrappers {
      colorizeOutput()
      buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
    }
  
    steps {
      shell('./jenkins.sh')
    }
  
    publishers {
      archiveJunit('spec/reports/*.xml,features/reports/*.xml,integration_features/reports/*.xml')
      slackNotifications {
        projectChannel('#jenkins')
        notifyFailure()
        notifyUnstable()
        notifyBackToNormal()
      }
    }
  }
}

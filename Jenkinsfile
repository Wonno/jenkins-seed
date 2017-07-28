#!/usr/bin/env groovy

pipeline {
  agent { label 'master' }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  stages {
    stage('Process Job DSL files') {
      when {
        branch 'master'
      }
      steps {
        jobDsl targets: 'jobs/*.groovy\njobs/**/*.groovy\nviews/*.groovy''',
          additionalClasspath: 'lib'
          removedJobAction: 'DELETE', removedViewAction: 'DELETE',
          unstableOnDeprecation: true,
      }
    }
  }

  post {
    always {
      deleteDir() // wipe the workspace
    }
    failure {
      slackSend(color: 'danger', message: "${env.JOB_NAME} #${env.BUILD_NUMBER} FAILURE (<${env.BUILD_URL}|Open>)")
    }
    unstable {
      slackSend(color: 'warning', message: "${env.JOB_NAME} #${env.BUILD_NUMBER} UNSTABLE (<${env.BUILD_URL}|Open>)")
    }
  }
}

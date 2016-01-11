use(conjur.Conventions) {
  def job = job('release_debian') {
    description('Upload a Debian package to Artifactory apt repo')

    parameters {
      stringParam('PROJECT_NAME', '', 'Jenkins job to grab the artifact from')
      stringParam('BUILD_NUMBER', 'lastSuccessfulBuild', 'Build to grab the artifact from')
      stringParam('ARTIFACT_PATTERN', '*.deb', 'Pattern used to select artifacts')
    }

    steps {
      copyArtifacts('$PROJECT_NAME') {
        includePatterns('$ARTIFACT_PATTERN')
        buildSelector {
          buildNumber('$BUILD_NUMBER')
        }
      }

      shell('''
      cat << YML > secrets.yml
      ARTIFACTORY_USERNAME: !var artifactory/users/jenkins/username
      ARTIFACTORY_PASSWORD: !var artifactory/users/jenkins/password
      YML
      '''.stripIndent())

      shell('''
        #!/bin/bash -e

        COMPONENT="testing"

        if [ "$GIT_BRANCH" == "origin/master" ]; then
          COMPONENT="stable"
        fi

        rm -f *latest*.deb
        summon debify publish -c $COMPONENT $ARTIFACT_PATTERN
      '''.stripIndent())
    }
  }
  job.applyCommonConfig()
  job.setBuildName('''
    #${BUILD_NUMBER}
    ${ENV,var="PROJECT_NAME"}
    #${ENV,var="BUILD_NUMBER"}
    ${ENV,var="ARTIFACT_PATTERN"}
  '''.stripIndent())
}

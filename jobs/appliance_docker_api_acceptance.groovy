use(conjur.Conventions) {
  def job = job('appliance-docker-api-acceptance') {
    description('Run API acceptance tests on Docker Conjur')

    parameters {
      stringParam('APPLIANCE_IMAGE', 'registry.tld/conjur-appliance', 'Appliance image id to test. Required.')
      stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
    }

    steps {
      shell('bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.1.5@appliance-docker-api-acceptance && export > rvm.env"')
      shell('''
        #!/bin/bash -e

        source rvm.env

        gem install bundler:1.11.2 && bundle install

        ./ci/bin/api-acceptance --log-level debug -i $APPLIANCE_IMAGE -t $APPLIANCE_IMAGE_TAG
      '''.stripIndent())
    }

    publishers {
      archiveJunit('ci/output/report/api-acceptance/*.xml')
      archiveArtifacts('ci/output/**')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/appliance.git', false)
  job.applyCommonConfig()
  job.setBuildName('#${BUILD_NUMBER} ${GIT_BRANCH}: ${ENV,var="APPLIANCE_IMAGE_TAG"}')
}

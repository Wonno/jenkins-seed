import utilities.Config

def job = job('appliance-docker-api-acceptance') {
  description('Run API acceptance tests on Docker Conjur')
  concurrentBuild()

  parameters {
    stringParam('APPLIANCE_IMAGE', 'registry.tld:80/conjur-appliance', 'Appliance image id to test. Required.')
    stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
  }

  wrappers {
    rvm('2.1.5@appliance-docker-api-acceptance')
  }

  steps {
    shell('''
      #!/bin/bash -e

      bundle install

      ./ci/bin/api-acceptance --log-level debug -i $APPLIANCE_IMAGE -t $APPLIANCE_IMAGE_TAG
    '''.stripIndent())
  }

  publishers {
    archiveJunit('ci/output/report/api-acceptance/*.xml')
    archiveArtifacts('ci/output/**')
  }
}

Config.addGitRepo(job, 'git@github.com:conjurinc/appliance.git', false)
Config.applyCommonConfig(job)

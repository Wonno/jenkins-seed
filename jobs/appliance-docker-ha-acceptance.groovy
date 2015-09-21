job('appliance-docker-ha-acceptance') {
  description('Run HA acceptance tests on Docker Conjur')
  label('docker && slave')
  logRotator(30, -1, -1, 5)

  scm {
    git('git@github.com:conjurinc/appliance.git')
  }

  parameters {
    stringParam('APPLIANCE_IMAGE', 'registry.tld:80/conjur-appliance', 'Appliance image id to test. Required.')
    stringParam('APPLIANCE_IMAGE_TAG', 'latest', 'Appliance image tag to test.')
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    rvm('2.1.5@appliance-docker-ha-acceptance')
    buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
  }

  steps {
    shell('''
      #!/bin/bash -e

      bundle install

      bootstrap_id=$(./ci/bin/ha-bootstrap --log-level debug -i $APPLIANCE_IMAGE -t $APPLIANCE_TAG)
      fixtures_id=$(./ci/bin/ha-fixtures --log-level debug $bootstrap_id)
      echo "Bootstrap id: $bootstrap_id"
      echo "Fixtures id:  $fixtures_id"
      rm -f ci/output/ha-acceptance/*
      ./ci/bin/ha-acceptance -l --log-level debug "$fixtures_id"
    '''.stripIndent())
  }

  publishers {
    archiveJunit('ci/output/report/ha-acceptance/*.xml')
    archiveArtifacts('ci/output/**')
  }
}

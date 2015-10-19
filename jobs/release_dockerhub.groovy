use(conjur.Conventions) {
  def job = job('release_dockerhub') {
    description('Publish an image to DockerHub through releasebot/pubbit')

    parameters {
      stringParam('DOCKER_IMAGE', '', 'The name of the image to promote.')
      stringParam('DOCKER_TAG', '', 'The tag to apply to the image.')
    }

    environmentVariables {
      env('RELEASE_SLAVE', 'release-slave.itci.conjur.net')
    }

    steps {
      shell('''
        env LOCAL_REGISTRY=$CONJUR_DOCKER_REGISTRY LOCAL_REGISTRY_USER=conjurinc \
        builder/bin/promote_docker_image $DOCKER_IMAGE $DOCKER_TAG
      '''.stripIndent())
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/pubbit.git', false)
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="DOCKER_IMAGE"}:${ENV,var="DOCKER_TAG"}')
}
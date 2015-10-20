use(conjur.Conventions) {
  def job = job('release_dockerhub') {
    description('Publish an image to DockerHub through releasebot/pubbit')

    parameters {
      stringParam('DOCKER_LOCAL_IMAGE', '', 'The name of the local image to promote.')
      stringParam('DOCKER_TAG', '', 'The tag to apply to the image.')
      stringParam('DOCKER_REMOTE_IMAGE', '', 'The name of the remote image.')
    }

    environmentVariables {
      env('RELEASE_SLAVE', 'release-slave.itci.conjur.net')
      env('LOCAL_REGISTRY', '$CONJUR_DOCKER_REGISTRY')
      env('LOCAL_REGISTRY_USER', 'conjurinc')
    }

    steps {
      shell('''
        builder/bin/promote_docker_image $DOCKER_LOCAL_IMAGE $DOCKER_TAG $DOCKER_REMOTE_IMAGE
      '''.stripIndent())
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/pubbit.git', false)
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="DOCKER_REMOTE_IMAGE"}:${ENV,var="DOCKER_TAG"}')
}

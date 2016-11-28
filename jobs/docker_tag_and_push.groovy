use(conjur.Conventions) {
  def job = job('docker_tag_and_push') {
    description('Utility job: Adds a new tag to an existing Docker image and pushes it to the registry')

    parameters {
      stringParam('IMAGE_NAME_CURRENT', '', 'Docker image name')
      stringParam('IMAGE_NAME_NEW', '', 'Docker image name')
      stringParam('IMAGE_TAG_CURRENT', '', 'Existing image tag')
      stringParam('IMAGE_TAG_NEW', '', 'New image tag')
    }

    steps {
      shell('''
        docker pull $IMAGE_NAME_CURRENT:$IMAGE_TAG_CURRENT
        docker tag -f $IMAGE_NAME_CURRENT:$IMAGE_TAG_CURRENT $IMAGE_NAME_NEW:$IMAGE_TAG_NEW
        docker push $IMAGE_NAME_NEW:$IMAGE_TAG_NEW
      '''.stripIndent())
    }
  }
  job.applyCommonConfig()
}

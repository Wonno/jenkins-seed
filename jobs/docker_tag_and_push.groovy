import utilities.Config

def job = job('docker_tag_and_push') {
  description('Adds a new tag to an existing Docker image and pushes it to the registry')

  parameters {
    stringParam('IMAGE_NAME', '', 'Docker image name')
    stringParam('IMAGE_TAG_CURRENT', '', 'Existing image tag')
    stringParam('IMAGE_TAG_NEW', '', 'New image tag')
    booleanParam('RELEASE_AMI', false, 'Trigger a downstream AMI release')
  }

  steps {
    shell('''
      docker tag $IMAGE_NAME:$IMAGE_TAG_CURRENT $IMAGE_NAME:$IMAGE_TAG_NEW
      docker push $IMAGE_NAME:$IMAGE_TAG_NEW
    '''.stripIndent())
  }
}

Config.applyCommonConfig(job)

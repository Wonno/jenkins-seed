import utilities.Config

def job = job('docker_tag_and_push') {
  description('Adds a new tag to an existing Docker image and pushes it to the registry')
  concurrentBuild()

  parameters {
    stringParam('IMAGE_NAME', '', 'Docker image name')
    stringParam('IMAGE_TAG_CURRENT', '', 'Existing image tag')
    stringParam('IMAGE_TAG_NEW', '', 'New image tag')
    booleanParam('RELEASE_AMI', false, 'Trigger a downstream AMI release')
  }

  steps {
    shell('''
      docker tag -f $IMAGE_NAME:$IMAGE_TAG_CURRENT $IMAGE_NAME:$IMAGE_TAG_NEW
      docker push $IMAGE_NAME:$IMAGE_TAG_NEW
    '''.stripIndent())

    conditionalSteps {
      condition {
        booleanCondition('$RELEASE_AMI')
      }
      steps {
        downstreamParameterized {
          trigger('appliance-docker-ami') {
            block {
                buildStepFailure('FAILURE')
                failure ('FAILURE')
                unstable('UNSTABLE')
            }
            parameters {
                predefinedProp('APPLIANCE_IMAGE', '$IMAGE_NAME')
                predefinedProp('APPLIANCE_IMAGE_TAG', '$IMAGE_TAG_NEW')
            }
          }
        }
      }
    }
  }
}

Config.applyCommonConfig(job)

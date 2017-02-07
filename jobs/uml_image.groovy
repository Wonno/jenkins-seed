use(conjur.Conventions) {
  def job = job('uml-image') {
    description('Convert Docker image to a SquashFS image for UML runtime.')
    
    parameters {
      stringParam('IMAGE', null, 'Tag of the docker image to convert, eg. registry.example/library/plan9:latest')
    }
    
    steps {
      shell('docker pull $IMAGE')
      shell('./mkimage.sh $IMAGE')
    }
    
    publishers {
      archiveArtifacts {
        pattern('*.sqsh')
      }
    }
  }
  
  job.addGitRepo('git@github.com:conjurinc/appliance-uml.git', false)
  job.applyCommonConfig(cleanup: false)
  job.setBuildName('$IMAGE')
}


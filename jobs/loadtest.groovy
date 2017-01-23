use(conjur.Conventions) {
  def job = job('loadtest') {
    description('Run load tests against a Conjur appliance on AWS')

    parameters {
      stringParam('IMAGE_ID', 'ami-98519cf5', 'Conjur AMI to use for target')
      stringParam('INSTANCE_TYPE', 'm3.medium', 'EC2 instance type')
      stringParam('REQUEST_RATE', '40', 'Requests/second')
      stringParam('DURATION', '2m', 'How long to run test for')
    }

    steps {
      shell('./test.sh $IMAGE_ID $INSTANCE_TYPE $REQUEST_RATE $DURATION')
    }

    publishers {
      archiveArtifacts('reports/*')
      publishHtml {
        report('reports') {
          reportFiles('out.html')
          reportName('Load Test')
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/loadtest.git', false)
  job.setBuildName('#${BUILD_NUMBER} ${ENV,var="IMAGE_ID"}-${ENV,var="INSTANCE_TYPE"}-${ENV,var="REQUEST_RATE"}req/sec-${ENV,var="DURATION"}')
}

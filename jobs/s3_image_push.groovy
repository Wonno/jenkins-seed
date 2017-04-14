use(conjur.Conventions) {
  def job = job('s3-image-push') {
    description('Pushes package container to S3 for distribution')

    parameters {
      stringParam('IMAGE_NAME', 'conjur-appliance', 'Name of the image to push to S3')
      stringParam('RELEASE_TAG', '', 'Release tag to push')
    }

    steps {

      shell('''
        set -e

        touch secrets.yml
        echo "AWS_ACCESS_KEY_ID: !var aws/ci/sys_powerful/access_key_id" >> secrets.yml
        echo "AWS_SECRET_ACCESS_KEY: !var aws/ci/sys_powerful/secret_access_key" >> secrets.yml

        docker pull registry.tld/$IMAGE_NAME:$RELEASE_TAG
        docker save registry.tld/$IMAGE_NAME:$RELEASE_TAG > $IMAGE_NAME-$RELEASE_TAG.tar
        summon docker run --env-file @SUMMONENVFILE -v $PWD:/share --rm anigeo/awscli s3 cp /share/$IMAGE_NAME-$RELEASE_TAG.tar s3://conjur-ci-images/docker/
      '''.stripIndent())

    }
  }
  job.applyCommonConfig()
}

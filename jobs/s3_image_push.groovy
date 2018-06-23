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
        echo "AWS_ACCESS_KEY_ID: !var ci/aws/iam/users/sys_powerful_conjurops_v2/access_key_id" >> secrets.yml
        echo "AWS_SECRET_ACCESS_KEY: !var ci/aws/iam/users/sys_powerful_conjurops_v2/secret_access_key" >> secrets.yml

        docker pull registry.tld/$IMAGE_NAME:$RELEASE_TAG
        docker save registry.tld/$IMAGE_NAME:$RELEASE_TAG | xz > $IMAGE_NAME-$RELEASE_TAG.tar.xz
        summon docker run --env-file @SUMMONENVFILE -v $PWD:/share --rm anigeo/awscli s3 cp /share/$IMAGE_NAME-$RELEASE_TAG.tar.xz s3://conjur-ci-images/docker/
      '''.stripIndent())
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
}

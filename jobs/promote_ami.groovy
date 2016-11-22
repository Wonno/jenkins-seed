use(conjur.Conventions) {
  def job = job('promote_ami') {
    description('Utility job: pushes a release canidate ami out to the various regions')

    parameters {
      stringParam('AMI', '', 'AMI ID to promote')
      stringParam('VERSION', '', 'Conjur Version Number')
    }

    steps {
      shell('''
        set -e

        touch secrets.yml
        echo "AWS_ACCESS_KEY_ID: !var aws/ci/sys_powerful/access_key_id" >> secrets.yml
        echo "AWS_SECRET_ACCESS_KEY: !var aws/ci/sys_powerful/secret_access_key" >> secrets.yml

        echo "Promoting to 'ci'"
        summon docker run --rm --env-file @SUMMONENVFILE -t conjurinc/ami-promoter \
        --ami $AMI \
        --regions 'us-west-1,us-west-2,eu-west-1' \
        --tags conjur/version=$VERSION

        echo "Promoting to 'dev'"
        summon docker run --rm --env-file @SUMMONENVFILE -t conjurinc/ami-promoter \
        --ami $AMI \
        --accounts 234457590086 \
        --regions 'us-east-1,us-west-1,us-west-2,eu-west-1' \
        --tags conjur/version=$VERSION

        echo "Promoting to 'prod'"
        summon docker run --rm --env-file @SUMMONENVFILE -t conjurinc/ami-promoter \
        --ami $AMI \
        --accounts 240734721936 \
        --regions 'us-east-1' \
        --tags conjur/version=$VERSION        
      '''.stripIndent())
    }
  }
  job.applyCommonConfig()
}

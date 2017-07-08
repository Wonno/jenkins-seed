use(conjur.Conventions) {
  def job = job('possum_website_app') {
    description('Build the s3-website docker image, used to serve the Possum website')

    steps {
      shell('summon ./jenkins.sh')
    }
  }

  job.addGitRepo('git@github.com:conjurinc/possum-website-app.git', false)
  job.applyCommonConfig()
}

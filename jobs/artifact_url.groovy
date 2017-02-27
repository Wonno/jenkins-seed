use(conjur.Conventions) {
  def job = job('artifact-url') {
    description('Get downloadable artifact URLs')

    steps {
      shell('docker build -t artifact-url .')
    }
  }
  job.applyCommonConfig(label: 'executor-v2')
  job.addGitRepo('git@github.com:conjurinc/artifact-url.git')
}

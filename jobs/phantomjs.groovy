use(conjur.Conventions) {
  def job = job('phantomjs') {
    description('''
      Build Conjur's version of phantomjs
    '''.stripIndent())

    steps {
      shell('./jenkins.sh')
      downstreamParameterized {
        trigger('docker_tag_and_push') {
          block()
          parameters {
            sameNode()
            predefinedProp('IMAGE_NAME_CURRENT', 'phantomjs')
            predefinedProp('IMAGE_NAME_NEW', 'registry.tld/phantomjs')
            predefinedProp('IMAGE_TAG_CURRENT', 'latest')
            predefinedProp('IMAGE_TAG_NEW', 'latest')
          }
        }
      }
    }
  }

  job.addGitRepo('git@github.com:conjurinc/phantomjs-docker.git')
  job.with {
    scm {
      // Configuring git directly overwrites the changes made by
      // addGitRepo, so do it this way, instead.
      configure {
        it / scm / recursiveSubmodules('true')
      }
    }
  }

  job.applyCommonConfig()
}

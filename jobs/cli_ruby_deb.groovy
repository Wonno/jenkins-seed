use(conjur.Conventions) {
  def job = job('cli-ruby-deb') {
    description('Builds pure Rubygems style non-Omnibus deb for the Ruby CLI, without using bundler')

    parameters {
      stringParam('DISTRIBUTION', 'conjurtools', 'apt distribution to push to')
    }

    wrappers {
      rvm('2.0.0@cli-ruby-deb')
    }

    steps {
      shell('''
      #!/bin/bash -e
      gem install -N bundler:1.11.2
      bundle

      ./build-deb.sh
      '''.stripIndent())
    }

    publishers {
      postBuildScripts {
        steps {
          shell('./publish.sh $DISTRIBUTION')
        }
        onlyIfBuildSucceeds(true)
      }
    }

    properties {
      promotions {
        promotion {
          name('Publish to apt stable component')
          conditions {
            manual('')
          }
          actions {
            shell('./publish.sh $DISTRIBUTION stable')
          }
        }
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

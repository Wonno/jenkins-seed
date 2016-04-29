use(conjur.Conventions) {
  def job = job('cli-ruby-deb-v5') {
    description('Builds pure Rubygems style non-Omnibus deb for the Ruby CLI, without using bundler')

    parameters {
      stringParam('DISTRIBUTION', '4.7', 'apt distribution to push to')
    }

    wrappers {
      rvm('2.0.0@cli-ruby-deb-v5')
    }
    
    steps {
      shell('''
      #!/bin/bash -e
      gem install -N bundler
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
          name("Publish to apt stable")
          icon("star-gold")
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

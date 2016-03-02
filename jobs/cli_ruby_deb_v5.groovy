use(conjur.Conventions) {
  def job = job('cli-ruby-deb-v5') {
    description('Builds pure Rubygems style non-Omnibus deb for the Ruby CLI, without using bundler')

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
          shell('./publish.sh 5.0')
        }
        onlyIfBuildSucceeds(true)
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

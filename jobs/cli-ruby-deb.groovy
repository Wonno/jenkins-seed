use(conjur.Conventions) {
  def job = job('cli-ruby-deb') {
    description('Builds a non-Omnibus deb for the Ruby CLI')

    parameters {
      stringParam('DISTRIBUTION', 'conjurtools', 'apt distribution to push package to')
    }

    wrappers {
      rvm('2.0.0@cli-ruby-deb')
    }

    steps {
      shell('./build-deb.sh')
    }

    publishers {
      archiveJunit('spec/reports/*.xml, features/reports/*.xml, acceptance-features/reports/*.xml')
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
            shell('debify publish --component stable $DISTRIBUTION $PROMOTED_JOB_NAME')
          }
        }
      }
    }
  }
  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/cli-ruby.git')
}

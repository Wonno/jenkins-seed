def artifacts = '*.deb, *=*, *.properties, Gemfile*'

use(conjur.Conventions) {
  def job = job('ldap-sync') {
    description('Build and test ldap-sync')

    parameters {
      stringParam('DISTRIBUTION', 'conjurtools', 'apt distribution to push package to')
    }

    steps {
      shell('./jenkins.sh')
    }

    publishers {
      cobertura('spec/reports/coverage/*coverage.xml') {
        failNoReports(false)
      }
      archiveArtifacts('*.deb')
      archiveJunit('spec/reports/*.xml, features/reports/*.xml')
      postBuildScripts {
        steps {
          shell('./publish.sh $DISTRIBUTION')
        }
        onlyIfBuildSucceeds(true)
      }
    }
  }

  job.applyCommonConfig()
  job.addGitRepo('git@github.com:conjurinc/ldap-sync.git')
}

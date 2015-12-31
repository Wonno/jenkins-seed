use(conjur.Conventions) {
    def job = job('conjur-asset-dsl2') {
        description('Test the Conjur DSL2 plugin')

        steps {
            shell('./jenkins.sh')
        }

        publishers {
            archiveJunit('spec/reports/*.xml, features/reports/*.xml')
        }
    }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjur-asset-dsl2.git')
}
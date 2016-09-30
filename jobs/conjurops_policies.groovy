use(conjur.Conventions) {
    def job = job('conjurops-policies') {
        description('Repository for ConjurOps policies')

        steps {
            shell('./jenkins.sh')
        }

        publishers {
            // archiveJunit('spec/reports/*.xml, features/reports/*.xml')
        }
    }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjurops-policies.git')
}

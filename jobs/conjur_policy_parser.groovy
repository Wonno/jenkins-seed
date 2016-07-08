use(conjur.Conventions) {
    def job = job('conjur-policy-parser') {
        description('Test the policy parser')

        steps {
            shell('./jenkins.sh')
        }

        publishers {
            archiveJunit('spec/reports/*.xml, features/reports/*.xml')
        }

        properties {
            promotions {
                promotion {
                    name("Release to Rubygems")
                    icon("star-gold")
                    conditions {
                        manual('')
                    }
                    actions {
                        downstreamParameterized {
                            trigger("release-rubygems", "SUCCESS", false, ["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
                                predefinedProp("GEM_NAME","conjur-policy-parser")
                            }
                        }
                    }
                }
            }
        }
    }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjur-policy-parser.git')
}

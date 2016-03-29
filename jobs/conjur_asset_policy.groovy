use(conjur.Conventions) {
    def job = job('conjur-asset-policy') {
        description('Test the Conjur policy plugin')

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
                                predefinedProp("GEM_NAME","conjur-asset-policy")
                            }
                        }
                    }
                }
            }
        }
    }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjur-asset-policy.git')
}

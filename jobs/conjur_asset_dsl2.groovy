use(conjur.Conventions) {
    def job = job('conjur-asset-dsl2') {
        description('Test the Conjur DSL2 plugin')

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
                                predefinedProp("GEM_NAME","conjur-asset-dsl2")
                            }
                        }
                    }
                }
            }
        }
    }

    job.applyCommonConfig()
    job.addGitRepo('git@github.com:conjurinc/conjur-asset-dsl2.git')
}
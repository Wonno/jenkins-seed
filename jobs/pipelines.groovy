def pipelines = [
  [repo: 'conjurinc/jenkins-seed'],
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance'],
  [repo: 'conjurinc/appliance-docker-ami', buildName: 'appliance-docker-ami-pipeline'],
  [repo: 'cyberark/conjur'],
  [repo: 'conjurinc/possum-cpanel'],
  [repo: 'cyberark/summon'],
  [repo: 'cyberark/summon-conjur'],
  [repo: 'cyberark/api-go', buildName: 'api-go-pipeline'],
  [repo: 'cyberark/api-ruby', buildName: 'api-ruby-pipeline'],
  [repo: 'cyberark/cli-ruby', buildName: 'cli-ruby-pipeline'],
  [repo: 'conjurinc/nginx'],
  [repo: 'conjur/puppet'],
  [repo: 'conjurinc/github_hooks'],
  [repo: 'conjurinc/java-example'],
  [repo: 'conjurinc/authn'],
  [repo: 'cyberark/api-java'],
  [repo: 'conjurinc/conjurops-policies']
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def gitHubUrl = "https://github.com/${githubOrg}/${githubRepoName}"

  def buildName = (pipeline.buildName == null) ? githubRepoName : pipeline.buildName

  multibranchPipelineJob(buildName) {
    description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

    triggers {
      periodic(1440)  // scan sources every 24hr, as a fallback - unit is minutes
    }

    branchSources {
      git {
        remote("git@github.com:${githubOrg}/${githubRepoName}.git")
        credentialsId('conjur-jenkins')
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        daysToKeep(3)  // remove merged pipelines every 3 days
      }
    }
  }
}

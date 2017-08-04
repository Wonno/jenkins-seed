def pipelines = [
  [repo: 'conjurinc/jenkins-seed'],

  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance'],
  [repo: 'conjurinc/appliance-docker-ami', buildName: 'appliance-docker-ami-pipeline'],
  [repo: 'conjurinc/possum'],
  [repo: 'conjurinc/possum-cpanel'],
  [repo: 'conjurinc/summon'],
  [repo: 'conjurinc/summon-conjur'],
  [repo: 'conjurinc/api-go', buildName: 'api-go-pipeline'],
  [repo: 'conjurinc/api-ruby', buildName: 'api-ruby-pipeline'],
  [repo: 'conjurinc/cli-ruby', buildName: 'cli-ruby-pipeline'],
  [repo: 'conjurinc/nginx'],
  [repo: 'conjur/puppet'],
  [repo: 'conjurinc/github_hooks'],
  [repo: 'conjurinc/java-example'],
  [repo: 'conjurinc/authn'],
  [repo: 'conjurinc/api-java'],
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

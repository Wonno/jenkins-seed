def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance'],
  [repo: 'conjurinc/possum'],
  [repo: 'conjurinc/possum-cpanel'],
  [repo: 'conjurinc/summon'],
  [repo: 'conjurinc/summon-conjur'],
  [repo: 'conjurinc/api-ruby', buildName: 'api-ruby-pipeline'],
  [repo: 'conjurinc/cli-ruby', buildName: 'cli-ruby-pipeline'],
  [repo: 'conjurinc/nginx'],
  [repo: 'conjur/puppet'],
  [repo: 'conjurinc/github_hooks'],
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def gitHubUrl = "https://github.com/${githubOrg}/${githubRepoName}"

  def buildName = (pipeline.buildName == null) ? githubRepoName : pipeline.buildName

  multibranchPipelineJob(buildName) {
    description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

    triggers {
      periodic(480)  // scan sources every 8hr, as a fallback - unit is minutes
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

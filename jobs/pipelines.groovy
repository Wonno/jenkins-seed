def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance'],
  [repo: 'conjurinc/possum', buildName: 'possum-pipeline'],
  [repo: 'conjurinc/summon'],
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def gitHubUrl = "https://github.com/${githubOrg}/${githubRepoName}"

  def buildName = (pipeline.buildName == null) ? githubRepoName : pipeline.buildName

  multibranchPipelineJob(buildName) {
    description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

    branchSources {
      git {
        remote("git@github.com:${githubOrg}/${githubRepoName}.git")
        credentialsId('conjur-jenkins')
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        numToKeep(30)
      }
    }
  }
}

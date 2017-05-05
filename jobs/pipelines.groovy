def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui', buildName: 'conjur-ui-pipeline'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance']
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
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        numToKeep(30)
      }
    }

    configure { project ->
      project / 'healthMetrics' / 'com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric'
    }
  }
}

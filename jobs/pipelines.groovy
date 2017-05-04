def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui', buildName: 'conjur-ui-pipeline'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/appliance', ondemand: true]
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def gitHubUrl = "https://github.com/${githubOrg}/${githubRepoName}"

  def ondemand = (pipeline.ondemand == true)  // don't trigger this build on git changes, default false
  def buildName = (pipeline.buildName == null) ? githubRepoName : pipeline.buildName

  multibranchPipelineJob(buildName) {
    description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

    branchSources {
      git {
        remote("git@github.com:${githubOrg}/${githubRepoName}.git")
        ignoreOnPushNotifications(ondemand)
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

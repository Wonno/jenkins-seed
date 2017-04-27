def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def ondemand = (pipeline.ondemand == true)  // don't trigger this build on git changes, default false

  multibranchPipelineJob(githubRepoName) {

    branchSources {
      git {
        remote("git@github.com:${githubOrg}/${githubRepoName}.git")
        ignoreOnPushNotifications(ondemand)
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        numToKeep(20)
      }
    }

    configure { project ->
      project / 'healthMetrics' / 'com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric'
    }
  }
}

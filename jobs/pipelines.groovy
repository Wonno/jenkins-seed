def pipelines = [
  [repo: 'conjurinc/appliance-uml'],
  [repo: 'conjurinc/conjur-ui', buildName: 'conjur-ui-pipeline'],
  [repo: 'conjurinc/apidocs'],
]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def ondemand = (pipeline.ondemand == true)  // don't trigger this build on git changes, default false
  def buildName = (pipeline.buildName == null) ? githubRepoName : pipeline.buildName

  multibranchPipelineJob(buildName) {

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

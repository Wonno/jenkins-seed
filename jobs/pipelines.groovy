def pipelines = [
  [name: 'conjurinc/appliance-uml']
]

pipelines.each { pipeline ->
  def (owner, repoName) =  pipeline.name.split('/')
  multibranchPipelineJob(pipeline.repoName) {
    branchSources {
      github {
        repoOwner(owner)
        repository(repoName)
        scanCredentialsId('conjur-jenkins')
      }
    }
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(20)
      }
    }
  }
}

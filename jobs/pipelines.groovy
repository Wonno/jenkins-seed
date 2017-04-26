def pipelines = [
  [name: 'conjurinc/appliance-uml']
]

pipelines.each { pipeline ->
  def (owner, repoName) =  pipeline.name.split('/')
  multibranchPipelineJob(pipeline.name) {
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

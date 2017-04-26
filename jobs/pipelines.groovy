def pipelines = [
  [owner: 'conjurinc', name: 'appliance-uml']
]

pipelines.each { pipeline ->
  multibranchPipelineJob(pipeline.name) {
    branchSources {
      github {
        repoOwner(pipeline.owner)
        repository(pipeline.name)
        scanCredentialsId('conjur-jenkins')
      }
    }
  }
}

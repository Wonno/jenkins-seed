// Conjur Docker appliance delivery pipeline view
deliveryPipelineView('Appliance Pipeline (DSL)') {
  allowPipelineStart()
  updateInterval(5)
  pipelineInstances(10)

  showChangeLog()
  showPromotions()
  showTotalBuildTime()

  pipelines {
    component('Appliance Pipeline (DSL)', 'appliance-docker-build')
  }
}

// Conjur Docker appliance delivery pipeline view
deliveryPipelineView('Docker Appliance Pipeline') {
  allowPipelineStart()
  updateInterval(5)
  pipelineInstances(10)

  showChangeLog()
  showPromotions()
  showTotalBuildTime()

  pipelines {
    component('Docker Appliance Pipeline', 'appliance-docker-build')
  }
}

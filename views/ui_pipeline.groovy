// Conjur UI pipeline view
deliveryPipelineView('UI Pipeline') {
  allowPipelineStart()
  updateInterval(5)
  pipelineInstances(10)

  showChangeLog()
  showPromotions()
  showTotalBuildTime()

  pipelines {
    component('UI Pipeline', 'conjur_asset_ui')
  }
}

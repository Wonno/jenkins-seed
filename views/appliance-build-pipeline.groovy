// Conjur appliance build pipeline view

buildPipelineView('Conjur Docker Pipeline') {
  title('Conjur Docker Appliance build pipeline')
  filterExecutors()
  displayedBuilds(10)
  selectedJob('appliance-docker-build')
  showPipelineParameters()
  refreshFrequency(5)
}

// Conjur appliance build pipeline view

buildPipelineView('Docker Appliance Pipeline') {
  title('Conjur Docker Appliance build pipeline')
  filterExecutors()
  displayedBuilds(5)
  selectedJob('appliance_docker_build')
  refreshFrequency(5)
}

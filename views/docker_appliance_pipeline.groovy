// Conjur appliance build pipeline view

buildPipelineView('Docker Appliance Pipeline') {
  title('Docker Appliance')
  filterExecutors()
  displayedBuilds(5)
  selectedJob('appliance_docker_build')
  refreshFrequency(5)
  showPipelineParameters()
}

def possum_services = [
  'possum',
  'possum-cpanel',
  'possum_website_app',
  'api-ruby-pipeline',
  'api-ruby',
  'cli-ruby',
  'cli-ruby-pipeline',
  'cli-ruby-debpkg',
]

listView('V5 Possum Jobs') {
  description('Jobs related to the Possum project')
  filterBuildQueue()

  jobs {
    possum_services.each { service ->
      name(service)
    }
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

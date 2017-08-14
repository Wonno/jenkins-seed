def pipelines = [
  'cyberark--conjur',  // Conjur 5 ->

  // - CLIs
  'cyberark--conjur-cli',
  'cyberark--summon',
  'cyberark--summon-conjur',

  // - Integrations
  'cyberark--conjur-puppet',
  'cyberark--ansible-role-conjur',

  // - API clients
  'cyberark--conjur-api-dotnet',
  'cyberark--conjur-api-go',
  'cyberark--conjur-api-java',
  'cyberark--conjur-api-ruby',
]

listView('Conjur 5.x') {
  description('V5 Pipelines and Jobs')
  filterBuildQueue()

  jobs {
    pipelines.each { pipelineName ->
      name(pipelineName)
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

def jobs = [
  'conjur',  // Conjur 5 ->

  // - CLIs
  'conjur-cli',
  'summon',
  'summon-conjur',

  // - Integrations
  'conjur-puppet',
  'ansible-role-conjur',

  // - API clients
  'conjur-api-dotnet',
  'conjur-api-go',
  'conjur-api-java',
  'conjur-api-ruby',
]

listView('Conjur 5.x') {
  description('V5 Pipelines and Jobs')
  filterBuildQueue()

  jobs {
    jobs.each { jobName ->
      name(jobName)
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

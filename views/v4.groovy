def pipelines = [
  'conjurinc--appliance',
  // 'audit',
  'conjurinc--authn',
  // 'authn-ldap',
  // 'authn-local',
  // 'authn-tv',
  // 'authz',
  'conjurinc--core',
  // 'conjur-ui',
  // 'expiration',
  // 'evoke',
  // // 'glider',
  // 'host-factory',
  // 'ldap-server',
  'conjurinc--ldap-sync',
  // // 'policy-loader',
  // 'pubkeys',
  'conjurinc--rotation',
  // // 'cyberark'
  // 'conjur-ui',
  // 'cli-ruby-omnibus',
  'conjurinc--appliance-docker-ami',
  'conjurinc--appliance-uml',
]

listView('Conjur 4.x') {
  description('V4 Pipelines and Jobs')
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

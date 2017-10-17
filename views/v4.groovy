def pipelines = [
  'conjurinc--audit',
  'conjurinc--authn',
  'conjurinc--authn-ldap',
  'conjurinc--authn-local',
  'conjurinc--authn-tv',
  'authz',
  'conjurinc--core',
  'conjurinc--expiration',
  'evoke',
  'host-factory',
  'ldap-server',
  'conjurinc--ldap-sync',
  'pubkeys',
  'conjurinc--rotation',

  'conjurinc--appliance',
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

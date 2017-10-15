def pipelines = [
  'conjurinc/appliance',

  'conjurinc/authn-ldap',
  'conjurinc/authn',
  'conjurinc/audit',
  'conjurinc/authn-local',
  'conjurinc/authn-tv',
  'conjurinc/authz',
  'conjurinc/core',
  'conjurinc/expiration',
  'conjurinc/host-factory',
  'conjurinc/ldap-server',
  'conjurinc/ldap-sync',
  'conjurinc/pubkeys',
  'conjurinc/rotation',

  'conjurinc/appliance-docker-ami',
  'conjurinc/appliance-uml',
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

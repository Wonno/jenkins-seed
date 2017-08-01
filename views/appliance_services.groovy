def appliance_services = [
  'audit',
  'authn',  // pipelined
  'authn-ldap',
  'authn-local',
  'authn-tv',
  'authz',
  'core',
  'conjur-ui',
  'expiration',
  'evoke',
  // 'glider',
  'host-factory',
  'ldap-server',
  'ldap-sync',
  // 'policy-loader',
  'pubkeys',
  'rotation',
  // 'cyberark'
]

listView('Appliance Services') {
  description('Services built into the Conjur appliance')
  filterBuildQueue()

  jobs {
    appliance_services.each { service ->
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

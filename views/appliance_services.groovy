def appliance_services = [
  'appliance',
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
  'conjur-ui',
  'cli-ruby-omnibus'
]

listView('V4 Appliance Jobs') {
  description('Jobs related to the Appliance project')
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

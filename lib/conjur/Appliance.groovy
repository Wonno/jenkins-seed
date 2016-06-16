// Holds services to be built in the appliance
// This is needed so that different jobs can use the services list

class Appliance {
  def services = [
    'audit',
    'authn',
    'authn-ldap',
    'authn-local',
    'authn-tv',
    'authz',
    'core',
    'expiration',
    'evoke',
    'host-factory',
    'ldap-server',
    'ldap-sync',
    'policy-loader',
    'pubkeys',
    'rotation'
  ]
}
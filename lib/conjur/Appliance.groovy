// Holds services to be built in the appliance
// This is needed so that different jobs can use the services list
package conjur

class Appliance {
  static List getServices() {
    return [
      'audit',
      'authn',
      'authn-ldap',
      'authn-local',
      'authn-tv',
      'authz',
      'core',
      'expiration',
      'evoke',
      // 'glider',
      'host-factory',
      'ldap-server',
      'ldap-sync',
      'policy-loader',
      'pubkeys',
      'rotation',
      'cyberark'
    ]
  }
}

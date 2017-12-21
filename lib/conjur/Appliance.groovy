// Holds services to be built in the appliance
// This is needed so that different jobs can use the services list
// We're moving these to pipelines, jobs commented out are now pipelines
package conjur

class Appliance {
  static List getServices() {
    return [
      // 'audit',
      // 'authn',  // pipelined
      // 'authn-ldap',
      // 'authn-local',
      // 'authn-tv',
      // 'authz',
      // 'core',
      // 'expiration',
      // 'evoke',
      // 'glider',
      // 'host-factory',
      // 'ldap-server',
      // 'ldap-sync',
      'policy-loader',
      // 'pubkeys',
      // 'rotation',
      'cyberark'
    ]
  }
}

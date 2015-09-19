buildFlowJob('omnibus-conjur') {
  description('Builds deb, rpm and pkg packages for the Conjur CLI in parallel.')
  logRotator(30, -1, -1, 5)

  buildFlow('''
    parallel (
      { build("omnibus-conjur-centos", BUILD_VERSION: params["BUILD_VERSION"]) },
      { build("omnibus-conjur-osx", BUILD_VERSION: params["BUILD_VERSION"]) },
      { build("omnibus-conjur-ubuntu", BUILD_VERSION: params["BUILD_VERSION"]) },
    )
  '''.stripIndent())
}
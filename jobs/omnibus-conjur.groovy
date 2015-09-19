// Define the build flow to run following jobs in parallel
buildFlowJob('omnibus-conjur') {
  description('Builds deb, rpm and pkg packages for the Conjur CLI in parallel.')
  logRotator(30, -1, -1, 5)

  parameters {
    stringParam('BUILD_VERSION', 'LATEST', 'Version of the CLI to build')
  }

  buildFlow('''
    parallel (
      { build("omnibus-conjur-centos", BUILD_VERSION: params["BUILD_VERSION"]) },
      { build("omnibus-conjur-osx", BUILD_VERSION: params["BUILD_VERSION"]) },
      { build("omnibus-conjur-ubuntu", BUILD_VERSION: params["BUILD_VERSION"]) },
    )
  '''.stripIndent())
}

// centos and ubuntu jobs
['centos', 'ubuntu'].each { platform ->
  job("omnibus-conjur-${platform}") {
    description("Builds the Conjur CLI package for ${platform}")
    label('docker && slave')
    logRotator(30, -1, -1, 5)

    parameters {
      stringParam('BUILD_VERSION', 'LATEST', 'Version of the CLI to build')
    }

    scm {
      git('git@github.com:conjurinc/omnibus-conjur.git')
    }

    wrappers {
      preBuildCleanup()
      colorizeOutput()
      buildName('#${BUILD_NUMBER} ${GIT_BRANCH}')
    }

    steps {
      shell("""
        if [ \"\${BUILD_VERSION}\" == \"LATEST\" ]; then
          export BUILD_VERSION=\$(gem query -r -n conjur-cli | grep -oE \"[0-9]{1,2}.[0-9]{1,2}.[0-9]{1,2}\")
        fi

        make VERSION=\${BUILD_VERSION}-1 test-${platform}
      """.stripIndent())
    }

    publishers {
      archiveArtifacts('built/*')
    }
  }
}
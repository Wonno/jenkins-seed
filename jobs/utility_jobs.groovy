def folderName = '__utilities'

folder(folderName) {
  description('Utility jobs, run on a timer')
}

job("${folderName}/cleanup_docker") {
  description('Periodically removes stopped containers and dangling images')
  logRotator(-1, 30, -1, 30)
  concurrentBuild()

  parameters {
    labelParam('NODE_LABEL') {
      defaultValue('docker')
      description('Run job on all nodes with this label')
      allNodes('allCases', 'IgnoreOfflineNodeEligibility')
    }
  }

  wrappers {
    preBuildCleanup()
    colorizeOutput()
    timestamps()
  }

  triggers {
    cron('H 5 * * *') // 5am UTC, 12am EST
  }

  steps {
    shell('''
    df -h

    echo '----------'

    # Remove exited containers
    docker rm -f -v $(docker ps -a -q -f status=exited) || true

    # Remove dangling images
    docker rmi $(docker images -q --filter "dangling=true") || true

    # Remove old conjur-ui images, they're not cleaned up well by their jobs
    docker rmi -f $(docker images | grep -E 'conjurinc.*ui.*[23456] weeks ago' | tr -s ' ' | cut -d' ' -f3) || true

    echo '----------'

    df -h
    '''.stripIndent())
  }
}

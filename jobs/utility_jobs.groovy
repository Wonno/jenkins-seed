def folderName = '__utilities'

folder(folderName) {
  description('Utility jobs, run on a timer')
}

use(conjur.Conventions) {
  def j = job("${folderName}/cleanup-docker") {
    parameters {
      labelParam('NODE_LABEL') {
        defaultValue('executor-v2')
        description('Run job on all nodes with this label')
        allNodes('allCases', 'IgnoreOfflineNodeEligibility')
      }
    }
    triggers {
      cron('H 6 * * *') // 6am UTC, 1am EST
    }
    steps {
      shell('sudo systemctl restart docker')
      shell('docker system prune --all --force')
    }
  }
  j.applyCommonConfig(label: 'executor-v2')
}

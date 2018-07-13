def buildName = 'conjurinc--playroom'
def githubUrl = 'https://github.com/conjurinc/playroom'
def githubOrg = 'conjurinc'
def githubRepoName = 'playroom'

multibranchPipelineJob(buildName) {
  description("on GitHub: <a href=\"${githubUrl}\">${githubUrl}</a>")

  triggers {
    cron('@daily')  // scan sources every 36hr, as a fallback - unit is minutes
  }

  branchSources {
    github {
      id("owner-${githubOrg}:repo-${githubRepoName}")
      repoOwner(githubOrg)
      repository(githubRepoName)
      scanCredentialsId('conjur-jenkins-api')
      //traits {
      //  gitHubBranchDiscovery()
      //  gitHubTagDiscovery()
      //}
    }
  }

  orphanedItemStrategy {
    discardOldItems {
      daysToKeep(1)  // remove merged pipelines every day
    }
  }
}

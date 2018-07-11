def buildName = 'conjurinc--playroom'
def githubUrl = 'https://github.com/conjurinc/playroom'
def githubOrg = 'conjurinc'
def githubRepoName = 'playroom'

multibranchPipelineJob(buildName) {
  description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

  triggers {
    cron('@daily')  // scan sources every 36hr, as a fallback - unit is minutes
  }

  branchSources {
    git {
      id("owner-${githubOrg}:repo-${githubRepoName}")
      remote("git@github.com:${githubOrg}/${githubRepoName}.git")
      credentialsId('conjur-jenkins')
    }
  }

  orphanedItemStrategy {
    discardOldItems {
      daysToKeep(1)  // remove merged pipelines every day
    }
  }
}

def pipelines = [
  // 'cyberark' org
  [repo: 'cyberark/conjur'],  // Conjur 5 ->

  // - CLIs
  [repo: 'cyberark/conjur-cli'],
  [repo: 'cyberark/summon'],
  [repo: 'cyberark/summon-conjur'],
  [repo: 'cyberark/summon-file'],

  // - Integrations
  [repo: 'cyberark/conjur-puppet'],
  [repo: 'cyberark/ansible-role-conjur'],

  // - API clients
  [repo: 'cyberark/conjur-api-dotnet'],
  [repo: 'cyberark/conjur-api-go'],
  [repo: 'cyberark/conjur-api-java'],
  [repo: 'cyberark/conjur-api-ruby'],

  // 'conjurinc' org
  [repo: 'conjurinc/appliance'],  // Docker distribution
  [repo: 'conjurinc/conjur-ui'],  // Integrated UI
  [repo: 'conjurinc/appliance-uml'],  // UML/RPM distribution
  [repo: 'conjurinc/appliance-docker-ami'],  // AWS EC2 AMI
  [repo: 'conjurinc/jenkins-seed'],
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/possum-cpanel'],
  [repo: 'conjurinc/nginx'],
  [repo: 'conjurinc/github_hooks'],
  [repo: 'conjurinc/java-example'],
  [repo: 'conjurinc/authn'],
  [repo: 'conjurinc/conjurops-policies'],
  [repo: 'conjurinc/conjur-ce-ami'],
  [repo: 'conjurinc/joes-pipeline'],
  [repo: 'conjurinc/possum-website-app'],
  [repo: 'conjurinc/release-bot'],

  // 'conjur' org

  // 'conjur-cookbooks' org

]

pipelines.each { pipeline ->
  def (githubOrg, githubRepoName) = pipeline.repo.split('/')
  def gitHubUrl = "https://github.com/${githubOrg}/${githubRepoName}"

  def buildName = (pipeline.buildName == null) ? "${githubOrg}--${githubRepoName}" : pipeline.buildName

  multibranchPipelineJob(buildName) {
    description("on GitHub: <a href=\"${gitHubUrl}\">${gitHubUrl}</a>")

    triggers {
      periodic(1440)  // scan sources every 24hr, as a fallback - unit is minutes
    }

    branchSources {
      git {
        remote("git@github.com:${githubOrg}/${githubRepoName}.git")
        credentialsId('conjur-jenkins')
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        daysToKeep(3)  // remove merged pipelines every 3 days
      }
    }
  }
}

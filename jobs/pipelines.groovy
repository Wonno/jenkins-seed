def pipelines = [
  // 'cyberark' org
  [repo: 'cyberark/conjur'],  // Conjur 5 ->
  [repo: 'cyberark/conjur-aws'],

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

  // - Libraries
  [repo: 'cyberark/slosilo'],

  // - Internal tooling
  [repo: 'cyberark/seal'],

  // 'conjurinc' org

  // - Docker appliance distribution of Conjur
  [repo: 'conjurinc/appliance'],

  // - Services packaged into appliance
  [repo: 'conjurinc/audit'],
  [repo: 'conjurinc/authn'],
  [repo: 'conjurinc/authn-ldap'],
  [repo: 'conjurinc/authn-local'],
  [repo: 'conjurinc/authn-tv',],
  [repo: 'conjurinc/authz',],
  [repo: 'conjurinc/core'],
  [repo: 'conjurinc/expiration'],
  [repo: 'conjurinc/host-factory'],
  [repo: 'conjurinc/ldap-server'],
  [repo: 'conjurinc/ldap-sync'],
  [repo: 'conjurinc/pubkeys'],
  [repo: 'conjurinc/rotation'],

  [repo: 'conjurinc/conjur-ui'],  // v4 Integrated UI
  [repo: 'conjurinc/appliance-uml'],  // UML/RPM distribution
  [repo: 'conjurinc/appliance-docker-ami'],  // AWS EC2 AMI


  // - Internal tooling
  [repo: 'conjurinc/jenkins-seed'],
  [repo: 'conjurinc/conjurops-policies'],
  [repo: 'conjurinc/github_hooks'],
  [repo: 'conjurinc/publish-rubygem'],

  // - TODO order these
  [repo: 'conjurinc/apidocs'],
  [repo: 'conjurinc/possum-cpanel'],
  [repo: 'conjurinc/nginx'],
  [repo: 'conjurinc/java-example'],
  [repo: 'conjurinc/authn-k8s'],
  [repo: 'conjurinc/joes-pipeline'],
  [repo: 'conjurinc/possum-website-app'],
  [repo: 'conjurinc/release-bot'],
  [repo: 'conjurinc/developer-www'],
  [repo: 'conjurinc/ami-promoter'],
  [repo: 'conjurinc/debify'],
  [repo: 'conjurinc/cluster'],

  // 'conjur' org

  // 'conjur-cookbooks' org
  [repo: 'conjur-cookbooks/conjur'],
  [repo: 'conjur-cookbooks/sshd-service'],
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

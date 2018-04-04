def pipelines = [
  // 'cyberark' org
  [repo: 'cyberark/conjur'],  // Conjur 5 ->
  [repo: 'cyberark/conjur-aws'],
  [repo: 'cyberark/conjur-org'],  // conjur.org website

  // - CLIs
  [repo: 'cyberark/conjur-cli'],
  [repo: 'cyberark/summon'],
  [repo: 'cyberark/summon-conjur'],
  [repo: 'cyberark/summon-file'],
  [repo: 'conjurinc/summon-s3'],  // TODO: move this to cyberark org
  [repo: 'cyberark/summon-aws-secrets'],

  // - Integrations
  [repo: 'cyberark/conjur-puppet'],
  [repo: 'cyberark/ansible-conjur-host-identity'],
  [repo: 'cyberark/ansible-conjur-lookup-plugin'],
  [repo: 'cyberark/conjur-service-broker'],
  [repo: 'cyberark/cloudfoundry-conjur-buildpack'],

  // - API clients
  [repo: 'cyberark/conjur-api-dotnet'],
  [repo: 'cyberark/conjur-api-go'],
  [repo: 'cyberark/conjur-api-java'],
  [repo: 'cyberark/conjur-api-ruby'],

  // - Libraries
  [repo: 'cyberark/slosilo'],

  // - Internal tooling
  [repo: 'cyberark/seal'],
  [repo: 'cyberark/conjur-policy-generator'],

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
  [repo: 'conjurinc/evoke'],
  [repo: 'conjurinc/host-factory'],
  [repo: 'conjurinc/ldap-server'],
  [repo: 'conjurinc/ldap-sync'],
  [repo: 'conjurinc/pubkeys'],
  [repo: 'conjurinc/rotation'],

  [repo: 'conjurinc/conjur-ui'],  // v4 Integrated UI
  [repo: 'conjurinc/conjur-asset-ui'],  // builds deb for conjur/ui-backend service
  // [repo: 'conjurinc/appliance-uml'],  // UML/RPM distribution
  [repo: 'conjurinc/appliance-docker-ami'],  // AWS EC2 AMI


  // - Internal tooling
  [repo: 'conjurinc/jenkins-seed'],
  [repo: 'conjurinc/conjurops-policies'],
  [repo: 'conjurinc/github_hooks'],
  [repo: 'conjurinc/publish-rubygem'],
  [repo: 'conjurinc/informative-narwhal'],

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
  [repo: 'conjurinc/api-python'],

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
      periodic(2880)  // scan sources every 48hr, as a fallback - unit is minutes
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
}

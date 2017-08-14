def jobs = [
  'conjurinc/appliance',  // Docker distribution
  'conjurinc/conjur-ui',  // Integrated UI
  'conjurinc/appliance-uml',  // UML/RPM distribution
  'conjurinc/appliance-docker-ami',  // AWS EC2 AMI
  'conjurinc/jenkins-seed',
  'conjurinc/apidocs',
  'conjurinc/possum-cpanel',
  'conjurinc/nginx',
  'conjurinc/github_hooks',
  'conjurinc/java-example',
  'conjurinc/authn',
  'conjurinc/conjurops-policies'
]

listView('Conjur 5.x') {
  description('V5 Pipelines and Jobs')
  filterBuildQueue()

  jobs {
    jobs.each { jobName ->
      name(jobName)
    }
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}

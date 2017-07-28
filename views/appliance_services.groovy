import conjur.Appliance

def pipelines = ['authn']
def dsl_jobs = Appliance.getServices().each { service -> name(service) }

listView('Appliance Services') {
  description('Services built into the Conjur appliance')
  filterBuildQueue()

  jobs {
    pipelines + dsl_jobs
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

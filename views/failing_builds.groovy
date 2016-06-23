listView('Failing Builds') {
  description('Builds that are currently unstable or worse')

  jobFilters {
    status {
      status(Status.UNSTABLE)
      status(Status.FAILED)
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
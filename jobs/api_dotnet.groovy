def repoUrl = 'git@github.com:conjurinc/api-dotnet.git'
def projectName = 'api-dotnet'

use(conjur.Conventions) {
  def job = job(projectName) {
    description('''
      <p>Build, test and sign .NET API bindings.
      <p>
      <a href="https://github.com/conjurinc/api-dotnet/">
        https://github.com/conjurinc/api-dotnet/
      </a>
    '''.stripIndent())
    steps {
      shell("./jenkins.sh")
    }
    publishers {
      archiveXUnit {
        nUnit {
          pattern('TestResult.xml')
        }
      }
      archiveArtifacts('bin/*')
    }
  }

  job.applyCommonConfig(false)
  job.addGitRepo(repoUrl, true)
}

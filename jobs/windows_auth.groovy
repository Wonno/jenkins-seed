def repoUrl = 'git@github.com:conjurinc/windows-auth.git'
def projectName = 'windows-auth'

use(conjur.Conventions) {
  def job = job(projectName) {
    description("""
    <p>Build windows authorization binaries.
    <p>
    <a href="https://github.com/conjurinc/windows-auth/">
      https://github.com/conjurinc/windows-auth/
    </a>
  """.stripIndent())
    steps {
      shell("./jenkins.sh")
    }
    publishers {
      archiveArtifacts('bin/*')
      archiveXUnit {
        cTest {
          pattern('Testing/**/*.xml')
        }
      }
    }
  }

  job.applyCommonConfig {
    noPreBuildCleanup()
  }

  job.addGitRepo(repoUrl, true)
}

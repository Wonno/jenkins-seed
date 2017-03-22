import conjur.Appliance

def artifacts = '*.deb, *=*'

use(conjur.Conventions) {
  Appliance.getServices().each { service ->
    def serviceJob = job("${service}-v2") {
      description("""
        <p>Builds packages and tests ${service}.</p>
        <p>Created by 'appliance_services.groovy'</p>
      """.stripIndent())

      steps {
        if (service == 'authz') { // hacky workaround, needs Dockerized like other services
          shell('''
           bash -c "source ~/.rvm/scripts/rvm && rvm use --install --create 2.0.0@conjur-authz && export > rvm.env"
           source rvm.env
           ./jenkins.sh
          '''.stripIndent())
        } else {
          shell('''
            project_name="audit"
            appliance_tag="4.9-stable"
            conjur_container_pid="conjur_container_pid"

            function finish {
              if [[ -f $conjur_container_pid ]]; then
                docker rm -f $(cat $conjur_container_pid)
                rm $conjur_container_pid
              fi
            }
            trap finish EXIT

            # Build the tag name
            if [ -f VERSION ]; then
              version=$(cat VERSION)
              base_commit=$(git log --pretty='%h' VERSION | head -n 1)
              commit_count=$(git log $base_commit..HEAD --pretty='%h' | wc -l | sed 's/ //g')
              sha=$(git rev-parse --short HEAD)
              tag="${version}.${commit_count}-${sha}"
            else
              tag=$(git describe --long --tags --abbrev=7 --match 'v*.*.*' | sed -e 's/^v//')
            fi

            # Build the .deb package
            docker run \
                  --rm \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  -v $PWD:/src \
                  -w /src \
                  --entrypoint="" \
                  registry2.itci.conjur.net/debify:latest bundle install && debify package ${project_name} --

            deb_package="conjur-${project_name}_${tag}_amd64.deb"
            conjur_appliance="registry.tld/conjur-appliance-cuke-master:${appliance_tag}"
            container_name="conjur-testing"

            # Refresh image
            docker pull $conjur_appliance

            # Start Conjur Appliance for testing
            docker run -d --name $container_name --security-opt seccomp:unconfined --cidfile $conjur_container_pid $conjur_appliance

            # Add deb package and restart Conjur
            docker cp $deb_package $container_name:/tmp
            if docker exec $container_name dpkg --list | grep -q "conjur-${project_name}" ; then
              docker exec $container_name dpkg --force all --purge conjur-$project_name;
            fi
            docker exec $container_name rm -f /opt/conjur/etc/$project_name.conf
            docker exec $container_name dpkg --install /tmp/$deb_package
            docker exec $container_name sv stop conjur
            docker exec $container_name sv start conjur

            # Run tests
            docker exec $container_name bash -c "cd /opt/conjur/${project_name}; ci/test.sh"
          '''.stripIndent())
        }
      }

      publishers {
        archiveJunit('spec/reports/*.xml, features/reports/**/*.xml, reports/*.xml')
        postBuildScripts {
          steps {
            shell('''
              #!/bin/bash -ex

              export DEBUG=true
              export GLI_DEBUG=true

              DISTRIBUTION=$(cat VERSION_APPLIANCE)
              COMPONENT=$(echo \${GIT_BRANCH#origin/} | tr '/' '.')

              if [ "$COMPONENT" == "master" ] || [ "$COMPONENT" == "v$DISTRIBUTION" ]; then
                COMPONENT=stable
              fi

              echo "Publishing $JOB_NAME to distribution '$DISTRIBUTION', component '$COMPONENT'"

              debify publish --component $COMPONENT $DISTRIBUTION $JOB_NAME

              if [ -f VERSION ]; then
                VERSION="$(debify detect-version | tail -n 1)"
              else
                VERSION=$(git describe --long --tags --abbrev=7 --match 'v*.*.*' | sed -e 's/^v//')
              fi

              touch "DISTRIBUTION=\$DISTRIBUTION"
              touch "COMPONENT=\$COMPONENT"
              touch "VERSION=\$VERSION"
            '''.stripIndent())
          }
          onlyIfBuildSucceeds(true)
        }
        archiveArtifacts(artifacts)
      }

      configure { project ->
        project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' {
          projectNameList {
            string "${service}"
          }
        }
      }
    }
    serviceJob.applyCommonConfig()
    serviceJob.addGitRepo("git@github.com:conjurinc/${service}.git")
  }
}

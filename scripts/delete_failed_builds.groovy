// Deletes all failed builds
// This decreases CPU/RAM usage on the Jenkins master

import hudson.model.*
// For each job
for (item in Hudson.instance.getItems(Job))
{
  // check that job is not building
  if (!item.isBuilding())
  {
    for (build in item.getBuilds()) {
      // if the build failed, delete it
      if (build.result == Result.FAILURE) {
        echo "Deleting build ${build}"
        build.delete()
      }
    }
  }
}

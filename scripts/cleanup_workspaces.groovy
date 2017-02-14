// Deletes workspaces for all jobs that are not currently building
// This decreases CPU load by a lot if there are many workspaces

import hudson.model.*
// For each job
for (item in Hudson.instance.getItems(Job))
{
  jobName = item.getFullDisplayName()
  // check that job is not building
  if (!item.isBuilding())
  {
    println("Wiping out workspaces of job " + jobName)
    customWorkspace = item.getCustomWorkspace()
    println("Custom workspace = " + customWorkspace)

    for (node in Hudson.getInstance().getNodes())
    {
      println("  Node: " + node.getDisplayName())
      workspacePath = node.getWorkspaceFor(item)
      if (workspacePath == null)
      {
        println("    Could not get workspace path")
      }
      else
      {
        if (customWorkspace != null)
        {
          workspacePath = node.getRootPath().child(customWorkspace)
        }

        pathAsString = workspacePath.getRemote()
        if (workspacePath.exists())
        {
          "sudo rm -rf ${pathAsString}".execute().text
          //workspacePath.deleteRecursive()
          println("    Deleted from location " + pathAsString)
        }
        else
        {
          println("    Nothing to delete at " + pathAsString)
        }
      }
    }
  }
}

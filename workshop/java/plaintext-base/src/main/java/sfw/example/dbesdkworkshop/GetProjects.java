package sfw.example.dbesdkworkshop;

import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Project;

// There is one get-projects command
// for all of the "get projects by" searches
// the exact query is selected by which options are provided
@Command(name = "get-projects", description = "get projects.")
public class GetProjects implements Runnable {

  @Option( names = {"-p", "--project-name"}, required = false, description = "by project name")
  String project;
  @Option( names = {"-s", "--status"}, required = false, description = "by status")
  String status;
  @Option( names = {"-S", "--start"}, required = false, description = "by lower bound of project start")
  String startDate;
  @Option( names = {"-E", "--end"}, required = false, description = "by upper bound of project start")
  String endDate;
  @Option( names = {"-q", "--target-start"}, required = false, description = "by lower bound of project target")
  String startTarget;
  @Option( names = {"-Q", "--target-end"}, required = false, description = "by upper bound of project target")
  String endTarget;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    List<Project> results;
    if (project != null && status != null)
      throw new IllegalArgumentException("get-projects must not specify both project-name and status");
    else if (project != null)
      results = api.getProjectByName(project);
    else if (status != null)
      results = api.getProjectsByStatus(status, startDate, endDate, startTarget, endTarget);
    else
      results = api.ScanProjects();

    for (Project item : results)
      System.out.println(item);  }
}

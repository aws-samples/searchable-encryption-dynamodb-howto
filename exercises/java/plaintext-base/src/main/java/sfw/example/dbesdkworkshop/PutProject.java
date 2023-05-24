package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Project;

@Command(name = "put-project", description = "Adds a record to the database.")
public class PutProject implements Runnable {

  @Option( names = {"-N", "--name"}, required = true, description = "set name")
  String name;
  @Option( names = {"-s", "--status"}, required = true, description = "set status")
  String status;
  @Option( names = {"-S", "--start"}, required = true, description = "set startDate")
  String startDate;
  @Option( names = {"-P", "--description"}, required = true, description = "set description")
  String description;
  @Option( names = {"-t", "--target"}, required = true, description = "set target date")
  String targetDate;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Project(
      name,
      status,
      startDate,
      description,
      targetDate
    ));
  }
}

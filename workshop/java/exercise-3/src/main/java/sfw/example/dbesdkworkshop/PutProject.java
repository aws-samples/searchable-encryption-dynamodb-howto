package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Project;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

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
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    api.putItem(new Project(
      name,
      status,
      startDate,
      description,
      targetDate
    ));

    System.out.println("Project Added");
  }
}

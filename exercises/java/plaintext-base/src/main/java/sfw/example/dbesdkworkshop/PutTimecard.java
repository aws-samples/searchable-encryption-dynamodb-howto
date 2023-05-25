package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Timecard;

@Command(name = "put-timecard", description = "Adds a record to the database.")
public class PutTimecard implements Runnable {

  @Option( names = {"-p", "--project-name"}, required = true, description = "set projectName")
  String projectName;
  @Option( names = {"-s", "--start"}, required = true, description = "set startDate")
  String startDate;
  @Option( names = {"-n", "--employee-number"}, required = true, description = "set employeeNumber")
  String employeeNumber;
  @Option( names = {"-e", "--email"}, required = true, description = "set employeeEmail")
  String employeeEmail;
  @Option( names = {"-h", "--hours"}, required = true, description = "set hours")
  String hours;
  @Option( names = {"-R", "--role"}, required = true, description = "set role")
  String role;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Timecard(
      projectName,
      startDate,
      employeeNumber,
      employeeEmail,
      hours,
      role
    ));

    System.out.println("Timecard Added");
  }
}

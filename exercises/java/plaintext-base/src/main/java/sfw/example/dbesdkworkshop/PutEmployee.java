package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Employee;

@Command(name = "put-employee", description = "Adds a record to the database.")
public class PutEmployee implements Runnable {

  @Option( names = {"-E", "--employeeNumber"}, required = true, description = "set employeeNumber")
  String employeeNumber;
  @Option( names = {"-t", "--employeeTag"}, required = true, description = "set employeeTag")
  String employeeTag;
  @Option( names = {"-e", "--email"}, required = true, description = "set email")
  String email;
  @Option( names = {"-m", "--managerEmail"}, required = true, description = "set managerEmail")
  String managerEmail;
  @Option( names = {"-c", "--city"}, required = true, description = "set city")
  String city;
  @Option( names = {"-b", "--building"}, required = true, description = "set building")
  String building;
  @Option( names = {"-f", "--floor"}, required = true, description = "set floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = true, description = "set room")
  String room;
  @Option( names = {"-d", "--desk"}, required = true, description = "set desk")
  String desk;
  @Option( names = {"-n", "--name"}, required = true, description = "set name")
  String name;
  @Option( names = {"-T", "--title"}, required = true, description = "set title")
  String title;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Employee(
      employeeNumber,
      employeeTag,
      email,
      managerEmail,
      city,
      building,
      floor,
      room,
      desk,
      name,
      title
    ));
  }
}

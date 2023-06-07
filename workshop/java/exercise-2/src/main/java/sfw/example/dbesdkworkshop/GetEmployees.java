package sfw.example.dbesdkworkshop;

import java.util.List;
import java.lang.IllegalArgumentException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Employee;

// There is one get-employees command
// for all of the "get employees by" searches
// the exact query is selected by which options are provided
@Command(name = "get-employees", description = "get employees.")
public class GetEmployees implements Runnable {

  @Option( names = {"-e", "--employee-email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-n", "--employee-number"}, required = false, description = "by employee number")
  String id;
  @Option( names = {"-G", "--manager-email"}, required = false, description = "by manager email")
  String manager;

  @Option( names = {"-c", "--city"}, required = false, description = "by city")
  String city;
  @Option( names = {"-b", "--building"}, required = false, description = "by building")
  String building;
  @Option( names = {"-f", "--floor"}, required = false, description = "by floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = false, description = "by room")
  String room;
  @Option( names = {"-d", "--desk"}, required = false, description = "by desk")
  String desk;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    List<Employee> results;
    if (email != null)
      results = api.getEmployeeByEmail(email);
    else if (id != null)
      results = api.getEmployeeById(id);
    else if (manager != null)
      results = api.getEmployeeByManager(manager);
    else if (city != null)
      results = api.getEmployeeByCity(city, building, floor, room, desk);
    else
      results = api.ScanEmployees(building, floor, room, desk);

    for (Employee item : results)
      System.out.println(item);
  }
}

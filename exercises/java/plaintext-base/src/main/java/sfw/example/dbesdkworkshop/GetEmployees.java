package sfw.example.dbesdkworkshop;

import java.util.List;
import java.lang.IllegalArgumentException;
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
  @Option( names = {"-n", "--employee-number"}, required = false, description = "by id")
  String id;
  @Option( names = {"-G", "--manager-email"}, required = false, description = "by id")
  String manager;

  @Option( names = {"-c", "--city"}, required = false, description = "by id")
  String city;
  @Option( names = {"-b", "--building"}, required = false, description = "by id")
  String building;
  @Option( names = {"-f", "--floor"}, required = false, description = "by id")
  String floor;
  @Option( names = {"-r", "--room"}, required = false, description = "by id")
  String room;
  @Option( names = {"-d", "--desk"}, required = false, description = "by id")
  String desk;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    if (email != null) {
      final List<Employee> results = api.getEmployeeByEmail(email);
      System.out.println(results);
    } else if (id != null) {
      final List<Employee> results = api.getEmployeeById(id);
      System.out.println(results);
    } else if (manager != null) {
      final List<Employee> results = api.getEmployeeByManager(manager);
      System.out.println(results);
    } else if (city != null) {
      final List<Employee> results = api.getEmployeeByCity(city, building, floor, room, desk);
      System.out.println(results);
    } else {
      throw new IllegalArgumentException("get-employees must specify city, email, manager-email or employee-number");
    }
  }
}

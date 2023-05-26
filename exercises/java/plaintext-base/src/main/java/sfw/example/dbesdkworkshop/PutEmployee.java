package sfw.example.dbesdkworkshop;

import java.util.HashMap;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Employee;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

@Command(name = "put-employee", description = "Adds a record to the database.")
public class PutEmployee implements Runnable {

  @Option( names = {"-n", "--employee-number"}, required = true, description = "set employeeNumber")
  String employeeNumber;
  @Option( names = {"-e", "--employee-email"}, required = true, description = "set email")
  String email;
  @Option( names = {"-g", "--manager-email"}, required = true, description = "set manager email")
  String assigneeEmail;
  @Option( names = {"-c", "--city"}, required = true, description = "set city")
  String city;
  @Option( names = {"-b", "--building"}, required = true, description = "set building")
  String building;
  @Option( names = {"-f", "--floor"}, required = false, description = "set floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = false, description = "set room")
  String room;
  @Option( names = {"-d", "--desk"}, required = false, description = "set desk")
  String desk;
  @Option( names = {"-N", "--name"}, required = true, description = "set name")
  String name;
  @Option( names = {"-T", "--title"}, required = true, description = "set title")
  String title;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    HashMap<String, String> location = new HashMap<String, String>();
    location.put(CITY_NAME, city);
    location.put(BUILDING_NAME, building);
    location.put(FLOOR_NAME, floor);
    location.put(ROOM_NAME, room);
    location.put(DESK_NAME, desk);

    api.putItem(new Employee(
      employeeNumber,
      email,
      assigneeEmail,
      location,
      name,
      title
    ));

    System.out.println("Employee Added");
  }
}

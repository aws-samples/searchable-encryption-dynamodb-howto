package sfw.example.dbesdkworkshop;

import java.util.HashMap;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Meeting;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

@Command(name = "put-meeting", description = "Adds a record to the database.")
public class PutMeeting implements Runnable {

  @Option( names = {"-n", "--employee-number"}, required = true, description = "set employeeNumber")
  String employeeNumber;
  @Option( names = {"-s", "--start"}, required = true, description = "set startTime")
  String startTime;
  @Option( names = {"-e", "--employee-email"}, required = true, description = "set employeeEmail")
  String employeeEmail;
  @Option( names = {"-f", "--floor"}, required = true, description = "set floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = true, description = "set room")
  String room;
  @Option( names = {"-D", "--duration"}, required = true, description = "set duration")
  String duration;
  @Option( names = {"-a", "--attendees"}, required = true, description = "set attendees")
  String attendees;
  @Option( names = {"-j", "--subject"}, required = true, description = "set subject")
  String subject;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    HashMap<String, String> location = new HashMap<String, String>();
    location.put(FLOOR_NAME, floor);
    location.put(ROOM_NAME, room);

    api.putItem(new Meeting(
      employeeNumber,
      startTime,
      employeeEmail,
      location,
      duration,
      attendees,
      subject
    ));

    System.out.println("Meeting Added");
  }
}

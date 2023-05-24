package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Emeeting;

@Command(name = "put-meeting", description = "Adds a record to the database.")
public class PutEmeeting implements Runnable {

  @Option( names = {"-E", "--employeeNumber"}, required = true, description = "set employeeNumber")
  String employeeNumber;
  @Option( names = {"-s", "--startTime"}, required = true, description = "set startTime")
  String startTime;
  @Option( names = {"-e", "--employeeEmail"}, required = true, description = "set employeeEmail")
  String employeeEmail;
  @Option( names = {"-f", "--floor"}, required = true, description = "set floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = true, description = "set room")
  String room;
  @Option( names = {"-d", "--duration"}, required = true, description = "set duration")
  String duration;
  @Option( names = {"-a", "--attendees"}, required = true, description = "set attendees")
  String attendees;
  @Option( names = {"-S", "--subject"}, required = true, description = "set subject")
  String subject;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Emeeting(
      employeeNumber,
      startTime,
      employeeEmail,
      floor,
      room,
      duration,
      attendees,
      subject
    ));
  }
}

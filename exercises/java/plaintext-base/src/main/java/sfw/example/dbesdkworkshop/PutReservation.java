package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Reservation;

@Command(name = "put-reservation", description = "Adds a record to the database.")
public class PutReservation implements Runnable {

  @Option( names = {"-b", "--building"}, required = true, description = "set building")
  String building;
  @Option( names = {"-s", "--start"}, required = true, description = "set startTime")
  String startTime;
  @Option( names = {"-f", "--floor"}, required = true, description = "set floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = true, description = "set room")
  String room;
  @Option( names = {"-o", "--organizer-email"}, required = true, description = "set organizerEmail")
  String organizerEmail;
  @Option( names = {"-D", "--duration"}, required = true, description = "set duration")
  String duration;
  @Option( names = {"-a", "--attendees"}, required = true, description = "set attendees")
  String attendees;
  @Option( names = {"-j", "--subject"}, required = true, description = "set subject")
  String subject;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Reservation(
      building,
      startTime,
      floor,
      room,
      organizerEmail,
      duration,
      attendees,
      subject
    ));
  }
}

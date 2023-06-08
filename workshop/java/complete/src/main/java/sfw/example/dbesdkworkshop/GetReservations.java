package sfw.example.dbesdkworkshop;

import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Reservation;

// There is one get-reservations command
// for all of the "get reservations by" searches
// the exact query is selected by which options are provided
@Command(name = "get-reservations", description = "get reservations.")
public class GetReservations implements Runnable {

  @Option( names = {"-i", "--reservation-id"}, required = false, description = "by reservation id")
  String id;
  @Option( names = {"-o", "--organizer-email"}, required = false, description = "by  organizer email")
  String organizerEmail;
  @Option( names = {"-b", "--building"}, required = false, description = "by building")
  String building;

  @Option( names = {"-S", "--start"}, required = false, description = "lower bound for startTime")
  String startTime;
  @Option( names = {"-E", "--end"}, required = false, description = "upper bound for startTime")
  String endTime;

  @Option( names = {"-f", "--floor"}, required = false, description = "by floor")
  String floor;
  @Option( names = {"-r", "--room"}, required = false, description = "by room")
  String room;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    final List<Reservation> results;
    if (id != null)
      results = api.getReservationsById(id);
    else if (organizerEmail != null)
      results = api.getReservationsByEmail(organizerEmail, startTime, endTime, floor, room);
    else if (building != null)
      results = api.getReservationsByBuilding(building, startTime, endTime, floor, room);
    else
      results = api.ScanReservations(startTime, endTime, floor, room);

    System.out.println(Reservation.heading());
    for (Reservation item : results)
      System.out.println(item);
  }
}

package sfw.example.dbesdkworkshop;

import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Meeting;

// There is one get-meetings command
// for all of the "get meetings by" searches
// the exact query is selected by which options are provided
@Command(name = "get-meetings", description = "get meetings.")
public class GetMeetings implements Runnable {

  @Option( names = {"-S", "--start"}, required = false, description = "by start date")
  String startDate;
  @Option( names = {"-E", "--end"}, required = false, description = "by end date")
  String endDate;
  @Option( names = {"-e", "--employee-email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-n", "--employee-number"}, required = false, description = "by id")
  String id;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    List<Meeting> results;
    if (email != null && id != null)
      throw new IllegalArgumentException("get-meetings must not specify both email and employee-number");
    else if (email != null)
      results = api.getMeetingsByEmail(email, startDate, endDate);
    else if (id != null)
      results = api.getMeetingsById(id, startDate, endDate);
    else {
      System.out.println("\nWARNING : You are doing a full table scan. In real life, this would be very time consuming.\n");
      results = api.ScanMeetings(startDate, endDate);
    }

    System.out.println(Meeting.heading());
    for (Meeting item : results)
      System.out.println(item);
  }
}

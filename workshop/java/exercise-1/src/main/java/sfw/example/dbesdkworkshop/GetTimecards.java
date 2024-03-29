package sfw.example.dbesdkworkshop;

import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Timecard;

// There is one get-timecards command
// for all of the "get timecards by" searches
// the exact query is selected by which options are provided
@Command(name = "get-timecards", description = "get timecards.")
public class GetTimecards implements Runnable {
  
  @Option( names = {"-S", "--start"}, required = false, description = "by start date")
  String startDate;
  @Option( names = {"-E", "--end"}, required = false, description = "by end date")
  String endDate;
  @Option( names = {"-p", "--project-name"}, required = false, description = "by email")
  String name;
  @Option( names = {"-e", "--employee-email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-R", "--role"}, required = false, description = "by id")
  String role;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    List<Timecard> results;
    if (email != null && name != null)
      throw new IllegalArgumentException("get-timecards must not specify both employee-email and project-name");
    else if (email != null)
      results = api.getTimecardsByEmail(email, startDate, endDate, role);
    else if (name != null)
      results = api.getTimecardsByName(name, startDate, endDate, role);
    else {
      System.out.println("\nWARNING : You are doing a full table scan. In real life, this would be very time consuming.\n");
      results = api.ScanTimecards(startDate, endDate, role);
    }

    System.out.println(Timecard.heading());
    for (Timecard item : results)
      System.out.println(item);

  }
}

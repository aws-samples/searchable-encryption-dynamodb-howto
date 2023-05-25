package sfw.example.dbesdkworkshop;

import java.util.List;
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
  @Option( names = {"-e", "--employee-email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-n", "--employee-number"}, required = false, description = "by id")
  String id;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    if (email != null && id != null) {
      throw new IllegalArgumentException("get-timecards must not specify both email and employee-number");
    } else if (email != null) {
      // final List<Timecard> results = api.getTimecardsByEmail(email, startDate, endDate);
      // System.out.println(results);
    } else if (id != null) {
      // final List<Timecard> results = api.getTimecardsById(id, startDate, endDate);
      // System.out.println(results);
    } else {
      throw new IllegalArgumentException("get-timecards must specify either email or employee-number");
    }
  }
}

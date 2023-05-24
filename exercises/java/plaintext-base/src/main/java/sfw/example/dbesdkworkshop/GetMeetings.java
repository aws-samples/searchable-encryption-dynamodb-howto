package sfw.example.dbesdkworkshop;

import java.util.List;
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
  @Option( names = {"-e", "--email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-n", "--employee-number"}, required = false, description = "by id")
  String id;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    if (email != null && id != null) {
      throw new IllegalArgumentException("get-meetings must not specify both email and employee-number");
    } else if (email != null) {
      final List<Meeting> results = api.getMeetingsByEmail(email, startDate, endDate);
      System.out.println(results);
    } else if (id != null) {
      final List<Meeting> results = api.getMeetingsById(id, startDate, endDate);
      System.out.println(results);
    } else {
      throw new IllegalArgumentException("get-meetings must specify either email or employee-number");
    }
  }
}

package sfw.example.dbesdkworkshop;

import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Emeeting;

// There is one get-meetings command
// for all of the "get meetings by" searches
// the exact query is selected by which options are provided
@Command(name = "get-meetings", description = "get meetings.")
public class GetMeetings implements Runnable {

  @Option( names = {"-s", "--startDate"}, required = false, description = "by start date")
  String startDate;
  @Option( names = {"-e", "--endDate"}, required = false, description = "by end date")
  String endDate;
  @Option( names = {"-E", "--email"}, required = false, description = "by email")
  String email;
  @Option( names = {"-i", "--employee-id"}, required = false, description = "by id")
  String id;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    if (email != null && id != null) {
        // throw not both
    } else if (email != null) {
      final List<Emeeting> results = api.getMeetingsByEmail(email, startDate, endDate);
      System.out.println(results);
    } else if (id != null) {
      final List<Emeeting> results = api.getMeetingsById(id, startDate, endDate);
      System.out.println(results);
    } else {
      // throw at least one
    }
  }
}

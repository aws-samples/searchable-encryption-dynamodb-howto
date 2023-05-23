package sfw.example.dbesdkworkshop;

import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Emeeting;

@Command(name = "get-meetings", description = "get meetings.")
public class GetMeetings implements Runnable {

  // @Option( names = {"-s", "--startDate"}, required = true, description = "by start date")
  // String employeeNumber;
  // @Option( names = {"-e", "--endDate"}, required = true, description = "by end date")
  // String employeeNumber;
  @Option( names = {"-E", "--email"}, required = true, description = "by email")
  String email;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    final List<Emeeting> results = api.getMeetingsByDateAndEmail("", email);
    System.out.println(results);
  }
}

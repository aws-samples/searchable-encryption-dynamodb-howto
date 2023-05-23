package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Ticket;

@Command(name = "put-ticket", description = "Adds a record to the database.")
public class PutTicket implements Runnable {

  @Option( names = {"-t", "--ticketNumber"}, required = true, description = "set ticketNumber")
  String ticketNumber;
  @Option( names = {"-m", "--modifiedDate"}, required = true, description = "set modifiedDate")
  String modifiedDate;
  @Option( names = {"-a", "--authorEmail"}, required = true, description = "set authorEmail")
  String authorEmail;
  @Option( names = {"-A", "--assigneeEmail"}, required = true, description = "set assigneeEmail")
  String assigneeEmail;
  @Option( names = {"-s", "--severity"}, required = true, description = "set severity")
  String severity;
  @Option( names = {"-S", "--subject"}, required = true, description = "set subject")
  String subject;
  @Option( names = {"-m", "--message"}, required = true, description = "set message")
  String message;

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.putItem(new Ticket(
      ticketNumber,
      modifiedDate,
      authorEmail,
      assigneeEmail,
      severity,
      subject,
      message
    ));
  }
}

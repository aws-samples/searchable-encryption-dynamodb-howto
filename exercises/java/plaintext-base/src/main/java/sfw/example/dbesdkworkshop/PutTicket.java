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

  @Option( names = {"-k", "--ticket-number"}, required = true, description = "set ticketNumber")
  String ticketNumber;
  @Option( names = {"-M", "--modified-date"}, required = true, description = "set modifiedDate")
  String modifiedDate;
  @Option( names = {"-A", "--author-email"}, required = true, description = "set authorEmail")
  String authorEmail;
  @Option( names = {"-g", "--assignee-email"}, required = true, description = "set assigneeEmail")
  String assigneeEmail;
  @Option( names = {"-v", "--severity"}, required = true, description = "set severity")
  String severity;
  @Option( names = {"-j", "--subject"}, required = true, description = "set subject")
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

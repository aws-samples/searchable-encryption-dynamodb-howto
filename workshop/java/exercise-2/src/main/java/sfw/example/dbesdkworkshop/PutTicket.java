package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Ticket;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

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
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    api.putItem(new Ticket(
      ticketNumber,
      modifiedDate,
      authorEmail,
      assigneeEmail,
      severity,
      subject,
      message
    ));

    System.out.println("Ticket Added");
  }
}

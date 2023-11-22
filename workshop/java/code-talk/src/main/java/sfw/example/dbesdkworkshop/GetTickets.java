package sfw.example.dbesdkworkshop;

import java.util.List;
import java.lang.IllegalArgumentException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.datamodel.Ticket;

// There is one get-Tickets command
// for all of the "get Tickets by" searches
// the exact query is selected by which options are provided
@Command(name = "get-tickets", description = "get tickets.")
public class GetTickets implements Runnable {

  @Option(names = { "-g", "--assignee-email" }, required = false, description = "by assignee")
  String assignee;
  @Option(names = { "-k", "--ticket-number" }, required = false, description = "by id")
  String ticket;
  @Option(names = { "-A", "--author-email" }, required = false, description = "by author")
  String author;
  @Option(names = { "-S", "--start" }, required = false, description = "by start time")
  String start;
  @Option(names = { "-E", "--end" }, required = false, description = "by end time")
  String end;
  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    List<Ticket> results;
    if (assignee != null)
      results = api.getTicketByAssignee(assignee, ticket, start, end);
    else if (author != null)
      results = api.getTicketByAuthor(author, ticket, start, end);
    else if (ticket != null)
      results = api.getTicketById(ticket, start, end);
    else {
      System.out.println("\nWARNING : You are doing a full table scan. In real life, this would be very time consuming.\n");
      results = api.ScanTickets(start, end);
    }

    System.out.println(Ticket.heading());
    for (Ticket item : results)
      System.out.println(item);
  }
}

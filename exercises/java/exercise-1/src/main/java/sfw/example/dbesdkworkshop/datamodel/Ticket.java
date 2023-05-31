package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class Ticket extends BaseItem {

  private String ticketNumber;
  private String modifiedDate;
  private String authorEmail;
  private String assigneeEmail;
  private String severity;
  private String subject;
  private String message;

  public Ticket(
      String ticketNumber,
      String modifiedDate,
      String authorEmail,
      String assigneeEmail,
      String severity,
      String subject,
      String message) {
    this.ticketNumber = ticketNumber;
    this.modifiedDate = modifiedDate;
    this.authorEmail = authorEmail;
    this.assigneeEmail = assigneeEmail;
    this.severity = severity;
    this.subject = subject;
    this.message = message;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(TICKET_NUMBER_PREFIX + ticketNumber));
    item.put(SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(AUTHOR_EMAIL_PREFIX + authorEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

    // item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(ASSIGNEE_EMAIL_PREFIX + assigneeEmail));

    // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(SEVERITY_PREFIX + severity));
    // item.put(GSI3_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));
// BEGIN EXERCISE 1 STEP 5

    item.put(TICKET_NUMBER_NAME, AttributeValue.fromS(ticketNumber));
    item.put(MODIFIED_DATE_NAME, AttributeValue.fromS(modifiedDate));
    item.put(AUTHOR_EMAIL_NAME, AttributeValue.fromS(authorEmail));
    item.put(ASSIGNEE_EMAIL_NAME, AttributeValue.fromS(assigneeEmail));
    item.put(SEVERITY_NAME, AttributeValue.fromS(severity));
    item.put(SUBJECT_NAME, AttributeValue.fromS(subject));
    item.put(MESSAGE_NAME, AttributeValue.fromS(message));
    return item;
  }

  public static Ticket fromItem(Map<String, AttributeValue> item) {
    return new Ticket(
        item.get(TICKET_NUMBER_NAME).s(),
        item.get(MODIFIED_DATE_NAME).s(),
        item.get(AUTHOR_EMAIL_NAME).s(),
        item.get(ASSIGNEE_EMAIL_NAME).s(),
        item.get(SEVERITY_NAME).s(),
        item.get(SUBJECT_NAME).s(),
        item.get(MESSAGE_NAME).s());
  }

  @Override
  public String toString() {
    return ticketNumber.toString() +
    "\t" + modifiedDate.toString() +
    "\t" + authorEmail.toString() +
    "\t" + assigneeEmail.toString() +
    "\t" + severity.toString() +
    "\t" + subject.toString() +
    "\t" + message.toString();
  }

}

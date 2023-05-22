package sfw.example.dbesdkworkshop.datamodel;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class Ticket extends BaseItem {

  private String ticketNumber;
  private String modifiedDate;
  private String authorEmail;
  private String assigneeEmail;
  private String severity;
  private String subject;
  private String message;

  protected Ticket(
    String ticketNumber,
    String modifiedDate,
    String authorEmail,
    String assigneeEmail,
    String severity,
    String subject,
    String message
  ) {
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
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(ticketNumber));
    item.put("modifiedDate", AttributeValue.fromS(modifiedDate));
    item.put("authorEmail", AttributeValue.fromS(authorEmail));
    item.put("assigneeEmail", AttributeValue.fromS(assigneeEmail));
    item.put("severity", AttributeValue.fromS(severity));
    item.put("subject", AttributeValue.fromS(subject));
    item.put("message", AttributeValue.fromS(message));
    return item;
  }

  public static Ticket fromItem(Map<String, AttributeValue> item) {
    return new Ticket(
      item.get(PARTITION_KEY_NAME).s(),
      item.get("modifiedDate").s(),
      item.get("authorEmail").s(),
      item.get("assigneeEmail").s(),
      item.get("severity").s(),
      item.get("subject").s(),
      item.get("message").s()
    );
  }
}

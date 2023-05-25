package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS("T-" + ticketNumber));
    item.put(SORT_KEY_NAME, AttributeValue.fromS("M-" + modifiedDate));

    item.put(GSI1_PARTITION_KEY_NAME, AttributeValue.fromS("CE-" + authorEmail));
    item.put(GSI1_SORT_KEY_NAME, AttributeValue.fromS("M-" + modifiedDate));

    item.put(GSI2_PARTITION_KEY_NAME, AttributeValue.fromS("AE-" + assigneeEmail));

    item.put(GSI3_PARTITION_KEY_NAME, AttributeValue.fromS("V-" + severity));
    item.put(GSI3_SORT_KEY_NAME, AttributeValue.fromS("M-" + modifiedDate));

    item.put("ticketNumber", AttributeValue.fromS(ticketNumber));
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
        item.get("ticketNumber").s(),
        item.get("modifiedDate").s(),
        item.get("authorEmail").s(),
        item.get("assigneeEmail").s(),
        item.get("severity").s(),
        item.get("subject").s(),
        item.get("message").s());
  }
}

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Emeeting extends BaseItem {

  private String employeeNumber;
  private String startTime;
  private String employeeEmail;
  private String floor;
  private String room;
  private String managerEmail;
  private String duration;
  private String attendees;
  private String subject;

  protected Emeeting(
      String employeeNumber,
      String startTime,
      String employeeEmail,
      String floor,
      String room,
      String managerEmail,
      String duration,
      String attendees,
      String subject) {
    this.employeeNumber = employeeNumber;
    this.startTime = startTime;
    this.employeeEmail = employeeEmail;
    this.floor = floor;
    this.room = room;
    this.managerEmail = managerEmail;
    this.duration = duration;
    this.attendees = attendees;
    this.subject = subject;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(employeeNumber));
    // TODO, how???
    //    item.put(SORT_KEY_NAME, AttributeValue.fromS(employeeTag));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("floor", AttributeValue.fromS(floor));
    item.put("room", AttributeValue.fromS(room));
    item.put("managerEmail", AttributeValue.fromS(managerEmail));
    item.put("duration", AttributeValue.fromS(duration));
    item.put("attendees", AttributeValue.fromS(attendees));
    item.put("subject", AttributeValue.fromS(subject));
    return item;
  }

  public static Emeeting fromItem(Map<String, AttributeValue> item) {
    return new Emeeting(
        item.get(PARTITION_KEY_NAME).s(),
        item.get("startTime").s(),
        item.get("employeeEmail").s(),
        item.get("floor").s(),
        item.get("room").s(),
        item.get("managerEmail").s(),
        item.get("duration").s(),
        item.get("attendees").s(),
        item.get("subject").s());
  }
}

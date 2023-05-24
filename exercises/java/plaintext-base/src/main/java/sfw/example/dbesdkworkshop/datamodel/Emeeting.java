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
  private String duration;
  private String attendees;
  private String subject;

  public Emeeting(
      String employeeNumber,
      String startTime,
      String employeeEmail,
      String floor,
      String room,
      String duration,
      String attendees,
      String subject) {
    this.employeeNumber = employeeNumber;
    this.startTime = startTime;
    this.employeeEmail = employeeEmail;
    this.floor = floor;
    this.room = room;
    this.duration = duration;
    this.attendees = attendees;
    this.subject = subject;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("PK", AttributeValue.fromS(employeeNumber));
    item.put("SK", AttributeValue.fromS(startTime + "." + floor + "." + room));
    item.put("PK1", AttributeValue.fromS(employeeEmail));
    item.put("SK1", AttributeValue.fromS(startTime + "." + floor + "." + room));
    item.put("employeeNumber", AttributeValue.fromS(employeeNumber));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("floor", AttributeValue.fromS(floor));
    item.put("room", AttributeValue.fromS(room));
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
        item.get("duration").s(),
        item.get("attendees").s(),
        item.get("subject").s());
  }
}

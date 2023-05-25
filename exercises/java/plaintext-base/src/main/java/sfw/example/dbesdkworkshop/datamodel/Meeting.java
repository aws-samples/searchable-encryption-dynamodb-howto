package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Meeting extends BaseItem {

  private String employeeNumber;
  private String startTime;
  private String employeeEmail;
  private Map<String, String> location;
  private String duration;
  private String attendees;
  private String subject;

  public Meeting(
      String employeeNumber,
      String startTime,
      String employeeEmail,
      String floor,
      String room,
      String duration,
      String attendees,
      String subject)
  {
    Map<String, String> loc = new HashMap<>();
    loc.put("room", room);
    loc.put("floor", floor);

    this.employeeNumber = employeeNumber;
    this.startTime = startTime;
    this.employeeEmail = employeeEmail;
    this.location = loc;
    this.duration = duration;
    this.attendees = attendees;
    this.subject = subject;
  }

  public Map<String, AttributeValue> toItem() {
    String floor = location.get("floor");
    String room = location.get("room");
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS("E-" + employeeNumber));
    // TODO, I think we can unique on just start time
    // This moves all sensitive information out of the PK/SK
    item.put(SORT_KEY_NAME, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));
    item.put(GSI1_PARTITION_KEY_NAME, AttributeValue.fromS("EE-" + employeeEmail));
    item.put(GSI1_SORT_KEY_NAME, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));
    item.put("employeeNumber", AttributeValue.fromS(employeeNumber));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("location", AttributeValue.fromM(StringMapToAttr(location)));
    item.put("duration", AttributeValue.fromS(duration));
    item.put("attendees", AttributeValue.fromS(attendees));
    item.put("subject", AttributeValue.fromS(subject));
    return item;
  }

  public static Meeting fromItem(Map<String, AttributeValue> item) {
    Map<String, AttributeValue> loc = item.get("location").m();
    return new Meeting(
        item.get("employeeNumber").s(),
        item.get("startTime").s(),
        item.get("employeeEmail").s(),
        loc.get("floor").s(),
        loc.get("room").s(),
        item.get("duration").s(),
        item.get("attendees").s(),
        item.get("subject").s());
  }
}

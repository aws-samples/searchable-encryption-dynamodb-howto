package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Reservation extends BaseItem {

  private String id;
  private String building;
  private String startTime;
  private String floor;
  private String room;
  private String organizerEmail;
  private String duration;
  private String attendees;
  private String subject;

  public Reservation(
      String id,
      String building,
      String startTime,
      String floor,
      String room,
      String organizerEmail,
      String duration,
      String attendees,
      String subject
  ) {
    this.id = id;
    this.building = building;
    this.startTime = startTime;
    this.floor = floor;
    this.room = room;
    this.organizerEmail = organizerEmail;
    this.duration = duration;
    this.attendees = attendees;
    this.subject = subject;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    // Having a unique value that involves sensitive values
    // is an interesting problem.
    // How might you ensure uniqueness in such cases?
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS("R-" + id));
    item.put(SORT_KEY_NAME, AttributeValue.fromS("R-" + id ));

    item.put(GSI1_PARTITION_KEY_NAME, AttributeValue.fromS("O-" + organizerEmail));
    item.put(GSI1_SORT_KEY_NAME, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));

    item.put(GSI3_PARTITION_KEY_NAME, AttributeValue.fromS("B-" + building));
    item.put(GSI3_SORT_KEY_NAME, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("floor", AttributeValue.fromS(floor));
    item.put("room", AttributeValue.fromS(room));
    item.put("organizerEmail", AttributeValue.fromS(organizerEmail));
    item.put("duration", AttributeValue.fromS(duration));
    item.put("attendees", AttributeValue.fromS(attendees));
    item.put("subject", AttributeValue.fromS(subject));
    return item;
  }

  public static Reservation fromItem(Map<String, AttributeValue> item) {
    return new Reservation(
        item.get(PARTITION_KEY_NAME).s(),
        item.get("startTime").s(),
        item.get("startTime").s(),
        item.get("floor").s(),
        item.get("room").s(),
        item.get("organizerEmail").s(),
        item.get("duration").s(),
        item.get("attendees").s(),
        item.get("subject").s());
  }
}

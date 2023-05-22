package sfw.example.dbesdkworkshop.datamodel;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class Reservation extends BaseItem {

  private String building;
  private String startTime;
  private String floor;
  private String room;
  private String organizerEmail;
  private String duration;
  private String attendees;
  private String subject;
  protected Reservation(
    String building,
    String startTime,
    String floor,
    String room,
    String organizerEmail,
    String duration,
    String attendees,
    String subject
  ) {
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
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(building));
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
      item.get("floor").s(),
      item.get("room").s(),
      item.get("organizerEmail").s(),
      item.get("duration").s(),
      item.get("attendees").s(),
      item.get("subject").s()
    );
  }

}

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class Reservation extends BaseItem {

  private String reservation;
  private String startTime;
  private Map<String, String> location;
  private String organizerEmail;
  private String duration;
  private String attendees;
  private String subject;

  public Reservation(
      String reservation,
      String startTime,
      Map<String, String> location,
      String organizerEmail,
      String duration,
      String attendees,
      String subject
  ) {
    this.reservation = reservation;
    this.startTime = startTime;
    this.location = location;
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
    item.put(PARTITION_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));
    item.put(SORT_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));

    String floor = location.get("floor");
    String room = location.get("room");
    String building = location.get("building");
    item.put(GSI1_PARTITION_KEY, AttributeValue.fromS("OE-" + organizerEmail));
    item.put(GSI1_SORT_KEY, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));

    item.put(GSI3_PARTITION_KEY, AttributeValue.fromS("B-" + building));
    item.put(GSI3_SORT_KEY, AttributeValue.fromS("S-" + startTime + ".F-" + floor + ".R-" + room));
    item.put(RESERVATION_NAME, AttributeValue.fromS(reservation));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("location", AttributeValue.fromM(StringMapToAttr(location)));
    item.put("organizerEmail", AttributeValue.fromS(organizerEmail));
    item.put("duration", AttributeValue.fromS(duration));
    item.put("attendees", AttributeValue.fromS(attendees));
    item.put("subject", AttributeValue.fromS(subject));
    return item;
  }

  public static Reservation fromItem(Map<String, AttributeValue> item) {
    return new Reservation(
        item.get("reservation").s(),
        item.get("startTime").s(),
        AttrToStringMap(item.get("location").m()),
        item.get("organizerEmail").s(),
        item.get("duration").s(),
        item.get("attendees").s(),
        item.get("subject").s());
  }

  @Override
  public String toString() {
    return reservation.toString() +
    "\t" + startTime.toString() +
    "\t" + organizerEmail.toString() +
    "\t" + duration.toString() +
    "\t" + attendees.toString() +
    "\t" + subject.toString() +
    "\t" + location.toString();
  }
}


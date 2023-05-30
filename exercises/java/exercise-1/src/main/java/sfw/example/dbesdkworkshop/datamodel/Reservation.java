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

// BEGIN EXERCISE 1 STEP 7
    // String floor = location.get(FLOOR_NAME);
    // String room = location.get(ROOM_NAME);
    // String building = location.get(BUILDING_NAME);
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(ORGANIZER_EMAIL_PREFIX + organizerEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + SPLIT + FLOOR_PREFIX + floor + SPLIT + ROOM_PREFIX + room));

    // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(BUILDING_PREFIX + building));
    // item.put(GSI3_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + SPLIT + FLOOR_PREFIX + floor + SPLIT + ROOM_PREFIX + room));
// BEGIN EXERCISE 1 STEP 7
    item.put(RESERVATION_NAME, AttributeValue.fromS(reservation));
    item.put(START_TIME_NAME, AttributeValue.fromS(startTime));
    item.put(LOCATION_NAME, AttributeValue.fromM(StringMapToAttr(location)));
    item.put(ORGANIZER_EMAIL_NAME, AttributeValue.fromS(organizerEmail));
    item.put(DURATION_NAME, AttributeValue.fromS(duration));
    item.put(ATTENDEES_NAME, AttributeValue.fromS(attendees));
    item.put(SUBJECT_NAME, AttributeValue.fromS(subject));
    return item;
  }

  public static Reservation fromItem(Map<String, AttributeValue> item) {
    return new Reservation(
        item.get(RESERVATION_NAME).s(),
        item.get(START_TIME_NAME).s(),
        AttrToStringMap(item.get(LOCATION_NAME).m()),
        item.get(ORGANIZER_EMAIL_NAME).s(),
        item.get(DURATION_NAME).s(),
        item.get(ATTENDEES_NAME).s(),
        item.get(SUBJECT_NAME).s());
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


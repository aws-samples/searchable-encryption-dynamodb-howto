package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

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
      Map<String, String> location,
      String duration,
      String attendees,
      String subject)
  {
    this.employeeNumber = employeeNumber;
    this.startTime = startTime;
    this.employeeEmail = employeeEmail;
    this.location = location;
    this.duration = duration;
    this.attendees = attendees;
    this.subject = subject;
  }

  public Map<String, AttributeValue> toItem() {
    String floor = location.get(FLOOR_NAME);
    String room = location.get(ROOM_NAME);
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime ));

// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + SPLIT + FLOOR_PREFIX + floor + SPLIT + ROOM_PREFIX + room));
// BEGIN EXERCISE 1 STEP 5

    item.put(EMPLOYEE_NUMBER_NAME, AttributeValue.fromS(employeeNumber));
    item.put(START_TIME_NAME, AttributeValue.fromS(startTime));
    item.put(EMPLOYEE_EMAIL_NAME, AttributeValue.fromS(employeeEmail));
    item.put(LOCATION_NAME, AttributeValue.fromM(StringMapToAttr(location)));
    item.put(DURATION_NAME, AttributeValue.fromS(duration));
    item.put(ATTENDEES_NAME, AttributeValue.fromS(attendees));
    item.put(SUBJECT_NAME, AttributeValue.fromS(subject));
    return item;
  }

  public static Meeting fromItem(Map<String, AttributeValue> item) {
    Map<String, AttributeValue> loc = item.get(LOCATION_NAME).m();
    return new Meeting(
        item.get(EMPLOYEE_NUMBER_NAME).s(),
        item.get(START_TIME_NAME).s(),
        item.get(EMPLOYEE_EMAIL_NAME).s(),
        AttrToStringMap(item.get(LOCATION_NAME).m()),
        item.get(DURATION_NAME).s(),
        item.get(ATTENDEES_NAME).s(),
        item.get(SUBJECT_NAME).s());
  }

  @Override
  public String toString() {
    return employeeNumber +
    "\t" + employeeEmail +
    "\t" + startTime +
    "\t" + subject +
    "\t" + duration +
    "\t" + attendees +
    "\t" + location.toString();
  }
}


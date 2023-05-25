package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Timecard extends BaseItem {

  private String projectName;
  private String startDate;
  private String employeeNumber;
  private String employeeEmail;
  private String hours;
  private String role;

  public Timecard(
      String projectName,
      String startDate,
      String employeeNumber,
      String employeeEmail,
      String hours,
      String role
  ) {
    this.projectName = projectName;
    this.startDate = startDate;
    this.employeeNumber = employeeNumber;
    this.employeeEmail = employeeEmail;
    this.hours = hours;
    this.role = role;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS("P-" + projectName));
    item.put(SORT_KEY_NAME, AttributeValue.fromS("S-" + startDate + ".N-" + employeeNumber ));
    item.put("startDate", AttributeValue.fromS(startDate));
    item.put("employeeNumber", AttributeValue.fromS(employeeNumber));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("hours", AttributeValue.fromS(hours));
    item.put("role", AttributeValue.fromS(role));
    return item;
  }

  public static Timecard fromItem(Map<String, AttributeValue> item) {
    return new Timecard(
        item.get(PARTITION_KEY_NAME).s(),
        item.get("startDate").s(),
        item.get("employeeNumber").s(),
        item.get("employeeEmail").s(),
        item.get("hours").s(),
        item.get("role").s());
  }
}

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Timecard extends BaseItem {

  private String projectName;
  private String startDate;
  private String employeeEmail;
  private String hours;
  private String role;

  protected Timecard(
      String projectName, String startDate, String employeeEmail, String hours, String role) {
    this.projectName = projectName;
    this.startDate = startDate;
    this.employeeEmail = employeeEmail;
    this.hours = hours;
    this.role = role;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(projectName));
    item.put("startDate", AttributeValue.fromS(startDate));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("hours", AttributeValue.fromS(hours));
    item.put("role", AttributeValue.fromS(role));
    return item;
  }

  public static Timecard fromItem(Map<String, AttributeValue> item) {
    return new Timecard(
        item.get(PARTITION_KEY_NAME).s(),
        item.get("startDate").s(),
        item.get("employeeEmail").s(),
        item.get("hours").s(),
        item.get("role").s());
  }
}

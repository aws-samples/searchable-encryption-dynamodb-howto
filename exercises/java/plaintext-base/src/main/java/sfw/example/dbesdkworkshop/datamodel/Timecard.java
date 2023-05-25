package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class Timecard extends BaseItem {

  private String projectName;
  private String startTime;
  private String employeeNumber;
  private String employeeEmail;
  private String hours;
  private String role;

  public Timecard(
      String projectName,
      String startTime,
      String employeeNumber,
      String employeeEmail,
      String hours,
      String role
  ) {
    this.projectName = projectName;
    this.startTime = startTime;
    this.employeeNumber = employeeNumber;
    this.employeeEmail = employeeEmail;
    this.hours = hours;
    this.role = role;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS("P-" + projectName));
    item.put(SORT_KEY, AttributeValue.fromS("S-" + startTime + ".E-" + employeeNumber ));

    item.put("projectName", AttributeValue.fromS(projectName));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("employeeNumber", AttributeValue.fromS(employeeNumber));
    item.put("employeeEmail", AttributeValue.fromS(employeeEmail));
    item.put("hours", AttributeValue.fromS(hours));
    item.put("role", AttributeValue.fromS(role));
    return item;
  }

  public static Timecard fromItem(Map<String, AttributeValue> item) {
    return new Timecard(
        item.get("projectName").s(),
        item.get("startTime").s(),
        item.get("employeeNumber").s(),
        item.get("employeeEmail").s(),
        item.get("hours").s(),
        item.get("role").s());
  }
}

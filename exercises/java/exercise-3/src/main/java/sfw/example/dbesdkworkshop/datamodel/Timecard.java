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
    item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
    item.put(PROJECT_NAME_NAME, AttributeValue.fromS(projectName));
    item.put(START_TIME_NAME, AttributeValue.fromS(startTime));
    item.put(EMPLOYEE_NUMBER_NAME, AttributeValue.fromS(employeeNumber));
    item.put(EMPLOYEE_EMAIL_NAME, AttributeValue.fromS(employeeEmail));
    item.put(HOURS_NAME, AttributeValue.fromS(hours));
    item.put(ROLE_NAME, AttributeValue.fromS(role));
    return item;
  }

  public static Timecard fromItem(Map<String, AttributeValue> item) {
    return new Timecard(
        item.get(PROJECT_NAME_NAME).s(),
        item.get(START_TIME_NAME).s(),
        item.get(EMPLOYEE_NUMBER_NAME).s(),
        item.get(EMPLOYEE_EMAIL_NAME).s(),
        item.get(HOURS_NAME).s(),
        item.get(ROLE_NAME).s());
  }

  @Override
  public String toString() {
    return projectName.toString() +
    "\t" + startTime.toString() +
    "\t" + employeeNumber.toString() +
    "\t" + employeeEmail.toString() +
    "\t" + hours.toString() +
    "\t" + role.toString();
  }

}

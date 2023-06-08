package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class Employee extends BaseItem {
  private String employeeNumber;
  private String employeeEmail;
  private String managerEmail;
  private Map<String, String> location;
  private String name;
  private String title;

  public Employee(
      String employeeNumber,
      String employeeEmail,
      String managerEmail,
      Map<String, String> location,
      String name,
      String title
  ) {
    this.employeeNumber = employeeNumber;
    this.employeeEmail = employeeEmail;
    this.managerEmail = managerEmail;
    this.location = location;
    this.name = name;
    this.title = title;
  }

  public static String AppendStrWithPrefix(String base, String value, String prefix)
  {
    if (value == null) return base;
    if (base.isEmpty()) return prefix + value;
    return base + '.' + prefix + value;
  }
  public static String StringOrNull(Map<String, AttributeValue> m, String name)
  {
    AttributeValue v = m.get(name);
    if (v == null) return null;
    return v.s();
  }
  
  public Map<String, AttributeValue> toItem() {
    String locTag = "";
    locTag = AppendStrWithPrefix(locTag, location.get(BUILDING_NAME), BUILDING_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(FLOOR_NAME), FLOOR_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(ROOM_NAME), ROOM_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(DESK_NAME), DESK_PREFIX);

    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(SORT_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
    item.put(GSI1_SORT_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(MANAGER_EMAIL_PREFIX + managerEmail));
    item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(CITY_PREFIX + location.get(CITY_NAME)));
    item.put(GSI3_SORT_KEY, AttributeValue.fromS(locTag));
    item.put(EMPLOYEE_NUMBER_NAME, AttributeValue.fromS(employeeNumber));
    item.put(EMPLOYEE_EMAIL_NAME, AttributeValue.fromS(employeeEmail));
    item.put(MANAGER_EMAIL_NAME, AttributeValue.fromS(managerEmail));
    item.put(LOCATION_NAME, AttributeValue.fromM(StringMapToAttr(location)));
    item.put(EMPLOYEE_NAME_NAME, AttributeValue.fromS(name));
    item.put(TITLE_NAME, AttributeValue.fromS(title));
    return item;
  }

  public static Employee fromItem(Map<String, AttributeValue> item) {
    Map<String, AttributeValue> loc = item.get(LOCATION_NAME).m();
    return new Employee(
        item.get(EMPLOYEE_NUMBER_NAME).s(),
        item.get(EMPLOYEE_EMAIL_NAME).s(),
        item.get(MANAGER_EMAIL_NAME).s(),
        AttrToStringMap(item.get(LOCATION_NAME).m()),
        item.get(EMPLOYEE_NAME_NAME).s(),
        item.get(TITLE_NAME).s());
  }

  private static String format = "%-15s%-20s%-20s%-20s%-10s%s";

  @Override
  public String toString() {
    return String
      .format(format,
        employeeNumber,
        employeeEmail,
        managerEmail,
        name,
        title,
        location.toString()
      );
  }

  public static String heading() {
    return String
      .format(format,
        "employeeNumber",
        "employeeEmail",
        "managerEmail",
        "name",
        "title",
        "location"
      );
  }
}

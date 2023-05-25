package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Employee extends BaseItem {
  private String employeeNumber;
  private String email;
  private String managerEmail;
  private Map<String, String> location;
  private String name;
  private String title;

  public Employee(
      String employeeNumber,
      String email,
      String managerEmail,
      String city,
      String building,
      String floor,
      String room,
      String desk,
      String name,
      String title
  ) {
    Map<String, String> loc = new HashMap<>();
    if (city != null) loc.put("city", city);
    if (building != null) loc.put("building", building);
    if (floor != null) loc.put("floor", floor);
    if (room != null) loc.put("room", room);
    if (desk != null) loc.put("desk", desk);

    this.employeeNumber = employeeNumber;
    this.email = email;
    this.managerEmail = managerEmail;
    this.location = loc;
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
    locTag = AppendStrWithPrefix(locTag, location.get("building"), "B-");
    locTag = AppendStrWithPrefix(locTag, location.get("floor"), "F-");
    locTag = AppendStrWithPrefix(locTag, location.get("room"), "R-");
    locTag = AppendStrWithPrefix(locTag, location.get("desk"), "D-");

    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS("E-" + employeeNumber));
    item.put(SORT_KEY_NAME, AttributeValue.fromS("E-" + employeeNumber));
    item.put(GSI1_PARTITION_KEY_NAME, AttributeValue.fromS("EE-" + email));
    item.put(GSI1_SORT_KEY_NAME, AttributeValue.fromS("E-" + employeeNumber));
    item.put(GSI2_PARTITION_KEY_NAME, AttributeValue.fromS("ME-" + managerEmail));
    item.put(GSI3_PARTITION_KEY_NAME, AttributeValue.fromS("C-" + location.get("city")));
    item.put(GSI3_SORT_KEY_NAME, AttributeValue.fromS(locTag));
    item.put("employeeNumber", AttributeValue.fromS(employeeNumber));
    item.put("email", AttributeValue.fromS(email));
    item.put("managerEmail", AttributeValue.fromS(managerEmail));
    item.put("location", AttributeValue.fromM(StringMapToAttr(location)));
    item.put("name", AttributeValue.fromS(name));
    item.put("title", AttributeValue.fromS(title));
    return item;
  }

  public static Employee fromItem(Map<String, AttributeValue> item) {
    Map<String, AttributeValue> loc = item.get("location").m();
    return new Employee(
        item.get("employeeNumber").s(),
        item.get("email").s(),
        item.get("managerEmail").s(),
        StringOrNull(loc, "city"),
        StringOrNull(loc, "building"),
        StringOrNull(loc, "floor"),
        StringOrNull(loc, "room"),
        StringOrNull(loc, "desk"),
        item.get("name").s(),
        item.get("title").s());
  }
}

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Employee extends BaseItem {
  private String employeeNumber;
  private String employeeTag;
  private String email;
  private String managerEmail;
  private String city;
  private String building;
  private String floor;
  private String room;
  private String desk;
  private String name;
  private String title;

  public Employee(
      String employeeNumber,
      String employeeTag,
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
    this.employeeNumber = employeeNumber;
    this.employeeTag = employeeTag;
    this.email = email;
    this.managerEmail = managerEmail;
    this.city = city;
    this.building = building;
    this.floor = floor;
    this.room = room;
    this.desk = desk;
    this.name = name;
    this.title = title;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(employeeNumber));
    item.put(SORT_KEY_NAME, AttributeValue.fromS(employeeTag));
    item.put("email", AttributeValue.fromS(email));
    item.put("managerEmail", AttributeValue.fromS(managerEmail));
    item.put("city", AttributeValue.fromS(city));
    item.put("building", AttributeValue.fromS(building));
    item.put("floor", AttributeValue.fromS(floor));
    item.put("room", AttributeValue.fromS(room));
    item.put("desk", AttributeValue.fromS(desk));
    item.put("name", AttributeValue.fromS(name));
    item.put("title", AttributeValue.fromS(title));
    return item;
  }

  public static Employee fromItem(Map<String, AttributeValue> item) {
    return new Employee(
        item.get(PARTITION_KEY_NAME).s(),
        item.get(SORT_KEY_NAME).s(),
        item.get("email").s(),
        item.get("managerEmail").s(),
        item.get("city").s(),
        item.get("building").s(),
        item.get("floor").s(),
        item.get("room").s(),
        item.get("desk").s(),
        item.get("name").s(),
        item.get("title").s());
  }
}

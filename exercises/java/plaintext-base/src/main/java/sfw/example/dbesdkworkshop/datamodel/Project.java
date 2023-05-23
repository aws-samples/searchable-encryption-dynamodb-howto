package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Project extends BaseItem {
  private String name;
  private String status;
  private String startDate;
  private String description;
  private String targetDate;

  public Project(
      String name, String status, String startDate, String description, String targetDate) {
    this.name = name;
    this.status = status;
    this.startDate = startDate;
    this.description = description;
    this.targetDate = targetDate;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY_NAME, AttributeValue.fromS(name));
    item.put("status", AttributeValue.fromS(status));
    item.put("startDate", AttributeValue.fromS(startDate));
    item.put("description", AttributeValue.fromS(description));
    item.put("targetDate", AttributeValue.fromS(targetDate));
    return item;
  }

  public static Project fromItem(Map<String, AttributeValue> item) {
    return new Project(
        item.get(PARTITION_KEY_NAME).s(),
        item.get("status").s(),
        item.get("startDate").s(),
        item.get("description").s(),
        item.get("targetDate").s());
  }
}

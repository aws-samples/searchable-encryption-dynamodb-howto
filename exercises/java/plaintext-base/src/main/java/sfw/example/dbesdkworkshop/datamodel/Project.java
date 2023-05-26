package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class Project extends BaseItem {
  private String projectName;
  private String status;
  private String startTime;
  private String description;
  private String targetDate;

  public Project(
      String projectName, String status, String startTime, String description, String targetDate) {
    this.projectName = projectName;
    this.status = status;
    this.startTime = startTime;
    this.description = description;
    this.targetDate = targetDate;
  }

  public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();

    item.put(PARTITION_KEY, AttributeValue.fromS("P-" + projectName));
    item.put(SORT_KEY, AttributeValue.fromS("P-" + projectName));
    item.put(GSI1_PARTITION_KEY, AttributeValue.fromS("U-" + status));
    item.put(GSI1_SORT_KEY, AttributeValue.fromS("S-" + startTime));

    item.put("projectName", AttributeValue.fromS(projectName));
    item.put("status", AttributeValue.fromS(status));
    item.put("startTime", AttributeValue.fromS(startTime));
    item.put("description", AttributeValue.fromS(description));
    item.put("targetDate", AttributeValue.fromS(targetDate));
    return item;
  }

  public static Project fromItem(Map<String, AttributeValue> item) {
    return new Project(
        item.get("projectName").s(),
        item.get("status").s(),
        item.get("startTime").s(),
        item.get("description").s(),
        item.get("targetDate").s());
  }

  @Override
  public String toString() {
    return projectName +
    "\t" + status +
    "\t" + startTime +
    "\t" + description +
    "\t" + targetDate;
  }

}

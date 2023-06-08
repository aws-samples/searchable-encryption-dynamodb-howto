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

    item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    item.put(SORT_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    item.put(PROJECT_NAME_NAME, AttributeValue.fromS(projectName));
    item.put(STATUS_NAME, AttributeValue.fromS(status));
    item.put(START_TIME_NAME, AttributeValue.fromS(startTime));
    item.put(DESCRIPTION_NAME, AttributeValue.fromS(description));
    item.put(TARGET_DATE_NAME, AttributeValue.fromS(targetDate));
    return item;
  }

  public static Project fromItem(Map<String, AttributeValue> item) {
    return new Project(
        item.get(PROJECT_NAME_NAME).s(),
        item.get(STATUS_NAME).s(),
        item.get(START_TIME_NAME).s(),
        item.get(DESCRIPTION_NAME).s(),
        item.get(TARGET_DATE_NAME).s());
  }

  private static String format = "%-15s%-10s%-20s%-20s%-20s";

  @Override
  public String toString() {
    return String
      .format(format,
        projectName,
        status,
        startTime,
        description,
        targetDate
      );
  }

  public static String heading() {
    return String
      .format(format,
        "projectName",
        "status",
        "startTime",
        "description",
        "targetDate"
      );
  }


}

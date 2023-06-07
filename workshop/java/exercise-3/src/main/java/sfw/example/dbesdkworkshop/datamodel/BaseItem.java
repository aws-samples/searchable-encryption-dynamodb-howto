// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import sfw.example.dbesdkworkshop.Config;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

/**
 * Parent class for modeling items for the Employee Portal DynamoDB table. See {@link Meeting} for
 * items corresponding to Meetings. See {@link Employee} for items corresponding to Employees. See
 * {@link Project} for items corresponding to Projects. See {@link Reservation} for items
 * corresponding to Reservations. See {@link Ticket} for items corresponding to Tickets. See {@link
 * Timecard} for items corresponding to Time-cards.
 */
public abstract class BaseItem {


  /**
   * Transform this modeled item into a DynamoDB item ready to write to the table.
   *
   * @return the item in {@link Map} of ({@link String}, {@link AttributeValue}) pairs, ready to
   *     write.
   */
  public abstract Map<String, AttributeValue> toItem();

  public static Map<String, AttributeValue> getEmployeeKey(
      final String employeeNumber, final String employeeTag) {
    Map<String, AttributeValue> key = new HashMap<>();
    key.put(PARTITION_KEY, AttributeValue.fromS(employeeNumber));
    key.put(SORT_KEY, AttributeValue.fromS(employeeTag));
    return key;
  }

  public static Map<String, AttributeValue> StringMapToAttr(Map<String, String> m)
  {
    Map<String, AttributeValue> item = new HashMap<>();
    for (Map.Entry<String, String> entry : m.entrySet()) {
      item.put(entry.getKey(), AttributeValue.fromS(entry.getValue()));
    }
    return item;
  }

  public static Map<String, String> AttrToStringMap(Map<String, AttributeValue> m)
  {
    Map<String, String> item = new HashMap<>();
    for (Map.Entry<String, AttributeValue> entry : m.entrySet()) {
      item.put(entry.getKey(), entry.getValue().s());
    }
    return item;
  }
}

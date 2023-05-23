// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop.datamodel;

import java.util.HashMap;
import java.util.Map;
import sfw.example.dbesdkworkshop.Config;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Parent class for modeling items for the Employee Portal DynamoDB table. See {@link Emeeting} for
 * items corresponding to E Meetings. See {@link Employee} for items corresponding to Employees. See
 * {@link Project} for items corresponding to Projects. See {@link Reservation} for items
 * corresponding to Reservations. See {@link Ticket} for items corresponding to Tickets. See {@link
 * Timecard} for items corresponding to Time-cards.
 */
public abstract class BaseItem {

  protected static final String PARTITION_KEY_NAME =
      Config.contents.document_bucket.document_table.partition_key;
  protected static final String SORT_KEY_NAME =
      Config.contents.document_bucket.document_table.sort_key;

  protected static final String GSI1_PARTITION_KEY_NAME =
      Config.contents.document_bucket.document_table.partition_key;
  protected static final String GSI1_SORT_KEY_NAME =
      Config.contents.document_bucket.document_table.sort_key;

  protected static final String GSI2_PARTITION_KEY_NAME =
      Config.contents.document_bucket.document_table.partition_key;
  protected static final String GSI2_SORT_KEY_NAME =
      Config.contents.document_bucket.document_table.sort_key;

  protected static final String GSI3_PARTITION_KEY_NAME =
      Config.contents.document_bucket.document_table.partition_key;
  protected static final String GSI3_SORT_KEY_NAME =
      Config.contents.document_bucket.document_table.sort_key;

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
    key.put(PARTITION_KEY_NAME, AttributeValue.fromS(employeeNumber));
    key.put(SORT_KEY_NAME, AttributeValue.fromS(employeeTag));
    return key;
  }

  @Override
  public String toString() {
    return this.toItem().toString();
  }
}

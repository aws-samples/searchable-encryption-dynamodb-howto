// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import sfw.example.dbesdkworkshop.datamodel.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

/** Defines the public interface to the Document Bucket operations. */
public class Api {
  private final DynamoDbClient ddbClient;
  private final String tableName;

  /**
   * Construct a Document Bucket {@code Api} using the provided configuration.
   *
   * @param ddbClient the {@link DynamoDbClient} to use to interact with Amazon DynamoDB.
   * @param tableName the name of the Document Bucket table.
   */
  public Api(
      // ADD-ESDK-START: Add the ESDK Dependency
      // Maybe change this to take a keyring and wrap the client?
      DynamoDbClient ddbClient, String tableName) {
    this.ddbClient = ddbClient;
    this.tableName = tableName;
    // ADD-ESDK-START: Add the ESDK Dependency
  }

  /**
   * Writes a {@link BaseItem} item to the DynamoDB table.
   *
   * @param modeledItem the item to write.
   * @param <T> the subtype of item to write.
   * @return the actual item value written ({@link Map} of {@link String}:{@link AttributeValue}).
   */
  protected <T extends BaseItem> Map<String, AttributeValue> putItem(T modeledItem) {
    final Map<String, AttributeValue> ddbItem = modeledItem.toItem();
    final PutItemRequest request =
        PutItemRequest.builder().item(ddbItem).tableName(tableName).build();
    ddbClient.putItem(request);
    return ddbItem;
  }
  protected void deleteTable() {
    final DeleteTableRequest request =
        DeleteTableRequest.builder()
        .tableName(tableName)
        .build();
    ddbClient.deleteTable(request);
  }

  protected void createTable() {
    try {deleteTable();}
    catch (Exception e) {}

    final String partKey = Config.contents.document_bucket.document_table.partition_key;
    final String sortKey = Config.contents.document_bucket.document_table.sort_key;
    final KeySchemaElement partSchema = KeySchemaElement.builder().attributeName(partKey).keyType(KeyType.HASH).build();
    final KeySchemaElement sortSchema = KeySchemaElement.builder().attributeName(sortKey).keyType(KeyType.RANGE).build();
    final ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
    keySchema.add(partSchema);
    keySchema.add(sortSchema);
    final ArrayList<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>();
    attrs.add(AttributeDefinition.builder().attributeName(partKey).attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder().attributeName(sortKey).attributeType(ScalarAttributeType.S).build());
    final ProvisionedThroughput throughPut = ProvisionedThroughput.builder().readCapacityUnits(100L).writeCapacityUnits(100L).build();
    final CreateTableRequest request =
        CreateTableRequest.builder()
        .tableName(tableName)
        .keySchema(keySchema)
        .attributeDefinitions(attrs)
        .provisionedThroughput(throughPut)
        .build();
    ddbClient.createTable(request);
  }

  /**
   * Retrieves a {@link Employee} for the supplied key.
   *
   * <p>This Access pattern tests encrypt/decrypt
   *
   * @param employeeNumber the employeeNumber to fetch the {@link Employee}.
   * @param employeeTag the employeeTag to fetch the {@link Employee}.
   * @return the {@link Employee} found.
   */
  protected Employee getEmployee(final String employeeNumber, final String employeeTag) {
    Map<String, AttributeValue> key = BaseItem.getEmployeeKey(employeeNumber, employeeTag);

    final GetItemRequest request = GetItemRequest.builder().tableName(tableName).key(key).build();

    return Employee.fromItem(ddbClient.getItem(request).item());
  }

  /**
   * Lists all the items in the DynamoDB table.
   *
   * <p>This access pattern tests encrypt/decrypt
   *
   * <p>These correspond to items in the Employee Portal.
   *
   * @return the {@link Set} of {@link Map<String, AttributeValue>}s in the Employee Portal.
   */
  public Set<Map<String, AttributeValue>> list() {
    final ScanRequest request = ScanRequest.builder().tableName(tableName).build();
    final ScanResponse response = ddbClient.scan(request);

    return response.items().stream().collect(Collectors.toSet());
  }

  public List<Emeeting> getMeetingsByDateAndEmail(String date, String email)
  {
    final String partKey = Config.contents.document_bucket.document_table.partition_key;
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":PartValue", AttributeValue.builder()
        .s(email) 
        .build());

    final QueryRequest request =
    QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(partKey + " = :PartValue")
    // .filterExpression("")
    .expressionAttributeValues(attrValues)
    .build();
    final QueryResponse result = ddbClient.query(request);
    final ArrayList<Emeeting> results = new ArrayList<Emeeting>();
    for (Map<String,AttributeValue> item : result.items()) {
      results.add(Emeeting.fromItem(item));
    }
    return results;
  }


  //PK1=email SK1 between(date1, date2)

  //  public List<Emeeting> getMeetingsByDateAndEmployeeId(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Emeeting> getMeetingsByDateAndBuilding(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Emeeting> getMeetingsByDateAndBuildingFloor(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Emeeting> getMeetingsByDateAndBuildingFloorRoom(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Emeeting> getMeetingsByEmail(){throw new IllegalArgumentException("not yet");}
  //
  //  public List<Employee> getEmployeeDataByEmail(){throw new IllegalArgumentException("not yet");}
  //  public List<Employee> getEmployeeInfoByEmployeeId(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Employee> getEmployeeInfoByEmail(){throw new IllegalArgumentException("not yet");}
  //  public List<Employee> getEmployeesByCity(){throw new IllegalArgumentException("not yet");}
  //  public List<Employee> getEmployeesByCityBuilding(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Employee> getEmployeesByCityBuildingFloor(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Employee> getEmployeesByCityBuildingFloorDesk(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Employee> getEmployeesByManagerEmail(){throw new IllegalArgumentException("not
  // yet");}
  //
  //  public List<Project> getProjectsByStatusStartAndTargetDate(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Project> getProjectsByName(){throw new IllegalArgumentException("not yet");}
  //  public List<Project> getProjectHistoryByDateRange(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Project> getProjectHistoryByRole(){throw new IllegalArgumentException("not yet");}
  //
  //  public List<Reservation> getReservationsByBuildingId(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Reservation> getReservationsByBuildingIdAndTimeRange(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Reservation> getReservationsByEmail(){throw new IllegalArgumentException("not
  // yet");}
  //
  //  public List<Ticket> getTicketsByEmail(){throw new IllegalArgumentException("not yet");}
  //  public List<Ticket> getTicketHistoryByTicketId(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Ticket> getTicketHistoryByEmployeeEmail(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Ticket> getTicketHistoryByAssigneeEmail(){throw new IllegalArgumentException("not
  // yet");}
  //  public List<Ticket> getAssignedTicketsByAssigneeEmail(){throw new
  // IllegalArgumentException("not yet");}
  //  public List<Ticket> getTicketsLastTouchedInThePast_24Hours(){throw new
  // IllegalArgumentException("not yet");}
  //
  //  public List<Timecard> getTimeCardsByEmail(){throw new IllegalArgumentException("not yet");}

}

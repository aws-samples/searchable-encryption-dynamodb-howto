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
import sfw.example.dbesdkworkshop.Config;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

/** Defines the public interface to the Document Bucket operations. */
public class Api {
  private final DynamoDbClient ddbClient;
  private final String tableName;

  protected static final String GSI1_NAME =
    Config.contents.ddb_table.gsi1_name;
  protected static final String GSI2_NAME =
    Config.contents.ddb_table.gsi2_name;
  protected static final String GSI3_NAME =
    Config.contents.ddb_table.gsi3_name;

  protected static final String PARTITION_KEY_NAME =
    Config.contents.ddb_table.partition_key;
  protected static final String SORT_KEY_NAME =
    Config.contents.ddb_table.sort_key;

  protected static final String GSI1_PARTITION_KEY_NAME =
    Config.contents.ddb_table.gsi1_partition_key;
  protected static final String GSI1_SORT_KEY_NAME =
    Config.contents.ddb_table.gsi1_sort_key;

  protected static final String GSI2_PARTITION_KEY_NAME =
    Config.contents.ddb_table.gsi2_partition_key;

  protected static final String GSI3_PARTITION_KEY_NAME =
    Config.contents.ddb_table.gsi3_partition_key;
  protected static final String GSI3_SORT_KEY_NAME =
    Config.contents.ddb_table.gsi3_sort_key;

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

    final Projection proj = Projection.builder().projectionType(ProjectionType.ALL).build();
    final ProvisionedThroughput throughPut = ProvisionedThroughput.builder().readCapacityUnits(100L).writeCapacityUnits(100L).build();
    final ArrayList<GlobalSecondaryIndex> gsi = new ArrayList<GlobalSecondaryIndex>();

    final KeySchemaElement pkSchema = KeySchemaElement
      .builder()
      .attributeName(PARTITION_KEY_NAME)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement skSchema = KeySchemaElement
      .builder()
      .attributeName(SORT_KEY_NAME)
      .keyType(KeyType.RANGE)
      .build();
    final ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
    keySchema.add(pkSchema);
    keySchema.add(skSchema);

    final KeySchemaElement pk1Schema = KeySchemaElement
      .builder()
      .attributeName(GSI1_PARTITION_KEY_NAME)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement sk1Schema = KeySchemaElement
      .builder()
      .attributeName(GSI1_SORT_KEY_NAME)
      .keyType(KeyType.RANGE)
      .build();
    final ArrayList<KeySchemaElement> gsi1Schema = new ArrayList<KeySchemaElement>();
    gsi1Schema.add(pk1Schema);
    gsi1Schema.add(sk1Schema);
    gsi.add(GlobalSecondaryIndex.builder()
      .indexName(GSI1_NAME)
      .keySchema(gsi1Schema)
      .provisionedThroughput(throughPut)
      .projection(proj)
      .build());

    final KeySchemaElement pk2Schema = KeySchemaElement
      .builder()
      .attributeName(GSI2_PARTITION_KEY_NAME)
      .keyType(KeyType.HASH)
      .build();
    final ArrayList<KeySchemaElement> gsi2Schema = new ArrayList<KeySchemaElement>();
    gsi2Schema.add(pk2Schema);
    gsi2Schema.add(skSchema);
    gsi.add(GlobalSecondaryIndex.builder()
      .indexName(GSI2_NAME)
      .keySchema(gsi2Schema)
      .provisionedThroughput(throughPut)
      .projection(proj)
      .build());

    final KeySchemaElement pk3Schema = KeySchemaElement
      .builder()
      .attributeName(GSI3_PARTITION_KEY_NAME)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement sk3Schema = KeySchemaElement
      .builder()
      .attributeName(GSI3_SORT_KEY_NAME)
      .keyType(KeyType.RANGE)
      .build();
    final ArrayList<KeySchemaElement> gsi3Schema = new ArrayList<KeySchemaElement>();
    gsi3Schema.add(pk3Schema);
    gsi3Schema.add(sk3Schema);
    gsi.add(GlobalSecondaryIndex.builder()
      .indexName(GSI3_NAME)
      .keySchema(gsi3Schema)
      .provisionedThroughput(throughPut)
      .projection(proj)
      .build());

    final ArrayList<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>();
    attrs.add(AttributeDefinition.builder()
      .attributeName(PARTITION_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(SORT_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(GSI1_PARTITION_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(GSI1_SORT_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(GSI2_PARTITION_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(GSI3_PARTITION_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(GSI3_SORT_KEY_NAME)
      .attributeType(ScalarAttributeType.S).build());
    final CreateTableRequest request =
        CreateTableRequest.builder()
        .tableName(tableName)
        .keySchema(keySchema)
        .attributeDefinitions(attrs)
        .provisionedThroughput(throughPut)
        .globalSecondaryIndexes(gsi)
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

  public String GetFilterForRange(String startDate, String endDate, String startVar, String endVar, String sk)
  {
    if (startDate != null && endDate != null) {
      return sk + " between " + startVar + " and " + endVar;
    } else if (startDate != null) {
      return sk + " >= " + startVar;
    } else if (endDate != null) {
      return sk + " <= " + endVar;
    } else {
      return null;
    }
  }

  public String GetKeyExprForRange(String startDate, String endDate, String sk)
  {
    if (startDate != null && endDate != null) {
      return " AND " + sk + " between :startDate and :endDate";
    } else if (startDate != null) {
      return " AND " + sk + " >= :startDate";
    } else if (endDate != null) {
      return " AND " + sk + " <= :endDate";
    } else {
      return "";
    }
  }


  public List<Meeting> getMeetingsByEmail(String email, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s("EE-" + email).build());
    AddValue(attrValues, ":startDate", startDate, "S-");
    AddValue(attrValues, ":endDate", endDate, "S-");
    String filterExpr = GetFilterForRange(startDate, endDate, ":startDate", ":endDate", SORT_KEY_NAME);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(GSI1_PARTITION_KEY_NAME + " = :email")
      .expressionAttributeValues(attrValues);
    if (filterExpr != null) builder = builder.filterExpression(filterExpr);
    return MeetingFromResp(ddbClient.query(builder.build()));
  }

  void AddValue(HashMap<String, AttributeValue> attrValues, String name, String value, String prefix)
  {
    if (value != null) {
      attrValues.put(name,
      AttributeValue.builder()
      .s(prefix + value) 
      .build());
    }
  }

  public List<Meeting> getMeetingsById(String id, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":id", AttributeValue.builder().s("E-" + id).build());
    AddValue(attrValues, ":startDate", startDate, "S-");
    AddValue(attrValues, ":endDate", endDate, "S-");
    String filterExpr = GetFilterForRange(startDate, endDate, ":startDate", ":endDate", SORT_KEY_NAME);
    
    QueryRequest.Builder builder = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY_NAME + " = :id")
    .expressionAttributeValues(attrValues);
    if (filterExpr != null) builder = builder.filterExpression(filterExpr);
    return MeetingFromResp(ddbClient.query(builder.build()));
  }

  protected List<Employee> EmployeeFromResp(QueryResponse resp) {
    final ArrayList<Employee> results = new ArrayList<Employee>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Employee.fromItem(item));
    }
    return results;
  }
  protected List<Ticket> TicketFromResp(QueryResponse resp) {
    final ArrayList<Ticket> results = new ArrayList<Ticket>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Ticket.fromItem(item));
    }
    return results;
  }
  protected List<Meeting> MeetingFromResp(QueryResponse resp) {
    final ArrayList<Meeting> results = new ArrayList<Meeting>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Meeting.fromItem(item));
    }
    return results;
  }
  protected List<Project> ProjectFromResp(QueryResponse resp) {
    final ArrayList<Project> results = new ArrayList<Project>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Project.fromItem(item));
    }
    return results;
  }

  public List<Employee> getEmployeeById(String id)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":id", AttributeValue.builder().s("E-" + id).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY_NAME + " = :id and SK = :id")
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public List<Project> getProjectByName(String name)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s("P-" + name).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY_NAME + " = :name and SK = :name")
    .expressionAttributeValues(attrValues)
    .build();

    return ProjectFromResp(ddbClient.query(request));
  }

  public List<Project> getProjectsByStatus(String status, String startDate, String endDate, String startTarget, String endTarget)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":status", AttributeValue.builder().s("U-" + status).build());
    AddValue(attrValues, ":startDate", startDate, "S-");
    AddValue(attrValues, ":endDate", endDate, "S-");
    AddValue(attrValues, ":startTarget", startTarget, "");
    AddValue(attrValues, ":endTarget", endTarget, "");
    String dateExpr = GetKeyExprForRange(startDate, endDate, GSI1_SORT_KEY_NAME);
    String filterExpr = GetFilterForRange(startTarget, endTarget, ":startTarget", ":endTarget", "targetDate");

    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(GSI1_PARTITION_KEY_NAME + " = :status" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (filterExpr != null) builder = builder.filterExpression(filterExpr);
    return ProjectFromResp(ddbClient.query(builder.build()));
  }

  public List<Employee> getEmployeeByEmail(String email)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s("EE-" + email).build());
    attrValues.put(":e", AttributeValue.builder().s("E-").build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI1_NAME)
    .keyConditionExpression(GSI1_PARTITION_KEY_NAME + " = :email and begins_with(" + GSI1_SORT_KEY_NAME + ", :e)")
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public List<Employee> getEmployeeByManager(String email)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s("ME-" + email).build());
    attrValues.put(":e", AttributeValue.builder().s("E-").build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI2_NAME)
    .keyConditionExpression(GSI2_PARTITION_KEY_NAME + " = :email and begins_with(SK, :e)")
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public static String AppendStrWithPrefix(String base, String value, String prefix)
  {
    if (value == null) return base;
    if (base.isEmpty()) return prefix + value;
    return base + '.' + prefix + value;
  }

  public List<Employee> getEmployeeByCity(String city, String building, String floor, String room, String desk)
  {
    String locTag = "";
    locTag = AppendStrWithPrefix(locTag, building, "B-");
    locTag = AppendStrWithPrefix(locTag, floor, "F-");
    locTag = AppendStrWithPrefix(locTag, room, "R-");
    locTag = AppendStrWithPrefix(locTag, desk, "D-");

    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":city", AttributeValue.builder().s("C-" + city).build());
    
    String keyExpr;
    if (locTag.isEmpty()) {
      keyExpr = GSI3_PARTITION_KEY_NAME + " = :city";
    } else {
      keyExpr = GSI3_PARTITION_KEY_NAME + " = :city and begins_with(SK3, :loc)";
      attrValues.put(":loc", AttributeValue.builder().s(locTag).build());
    }

    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI3_NAME)
    .keyConditionExpression(keyExpr)
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public List<Ticket> getTicketById(String ticket, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":ticket", AttributeValue.builder().s("T-" + ticket).build());
    AddValue(attrValues, ":startDate", startDate, "M-");
    AddValue(attrValues, ":endDate", endDate, "M-");
    String dateExpr = GetKeyExprForRange(startDate, endDate, SORT_KEY_NAME);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .keyConditionExpression(PARTITION_KEY_NAME + " = :ticket" + dateExpr)
      .expressionAttributeValues(attrValues);
    return TicketFromResp(ddbClient.query(builder.build()));
  }

  public List<Ticket> getTicketByAuthor(String author, String ticket, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":author", AttributeValue.builder().s("CE-" + author).build());
    AddValue(attrValues, ":startDate", startDate, "M-");
    AddValue(attrValues, ":endDate", endDate, "M-");
    AddValue(attrValues, ":ticket", ticket, "T-");
    String dateExpr = GetKeyExprForRange(startDate, endDate, GSI1_SORT_KEY_NAME);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(GSI1_PARTITION_KEY_NAME + " = :author" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (ticket != null) builder = builder.filterExpression(PARTITION_KEY_NAME + " = :ticket");
    return TicketFromResp(ddbClient.query(builder.build()));
  }

  public List<Ticket> getTicketByAssignee(String assignee, String ticket, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":assignee", AttributeValue.builder().s("AE-" + assignee).build());
    AddValue(attrValues, ":startDate", startDate, "M-");
    AddValue(attrValues, ":endDate", endDate, "M-");
    AddValue(attrValues, ":ticket", ticket, "T-");
    String dateExpr = GetKeyExprForRange(startDate, endDate, SORT_KEY_NAME);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI2_NAME)
      .keyConditionExpression(GSI2_PARTITION_KEY_NAME + " = :assignee" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (ticket != null) builder = builder.filterExpression(PARTITION_KEY_NAME + " = :ticket");
    return TicketFromResp(ddbClient.query(builder.build()));
  }
}

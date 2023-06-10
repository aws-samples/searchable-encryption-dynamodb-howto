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
import static sfw.example.dbesdkworkshop.AwsSupport.*;
import static sfw.example.dbesdkworkshop.Config.Constants.*;
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
    DynamoDbClient ddbClient, String tableName) {
    this.ddbClient = ddbClient;
    this.tableName = tableName;
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
    final Projection proj = Projection.builder().projectionType(ProjectionType.ALL).build();
    final ProvisionedThroughput throughPut = ProvisionedThroughput.builder().readCapacityUnits(100L).writeCapacityUnits(100L).build();
    final ArrayList<GlobalSecondaryIndex> gsi = new ArrayList<GlobalSecondaryIndex>();

    final KeySchemaElement pkSchema = KeySchemaElement
      .builder()
      .attributeName(PARTITION_KEY)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement skSchema = KeySchemaElement
      .builder()
      .attributeName(SORT_KEY)
      .keyType(KeyType.RANGE)
      .build();
    final ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
    keySchema.add(pkSchema);
    keySchema.add(skSchema);

    final KeySchemaElement pk1Schema = KeySchemaElement
      .builder()
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement sk1Schema = KeySchemaElement
      .builder()
    .attributeName(BEACON_PREFIX + GSI1_SORT_KEY)
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
      .attributeName(BEACON_PREFIX + GSI2_PARTITION_KEY)
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
      .attributeName(BEACON_PREFIX + GSI3_PARTITION_KEY)
      .keyType(KeyType.HASH)
      .build();
    final KeySchemaElement sk3Schema = KeySchemaElement
      .builder()
      .attributeName(BEACON_PREFIX + GSI3_SORT_KEY)
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
      .attributeName(PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(SORT_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI1_SORT_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI2_PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI3_PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI3_SORT_KEY)
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

  public String IncrString(String s)
  {
    return s + Character.MAX_VALUE;
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

  public List<Meeting> getMeetingsByEmail(String email, String startDate, String endDate, String floor, String room)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s(EMPLOYEE_EMAIL_PREFIX + email).build());
    attrValues.put(":pk", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX).build());
    String keyExpr = GSI1_PARTITION_KEY + " = :email";
    if (floor == null && room == null) {
      AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
      AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
      keyExpr += " and " + GSI1_SORT_KEY + " between :startDate and :endDate";
    } else {
      if (startDate == null)
        throw new IllegalArgumentException("get-meetings by email, with floor or room, must also have --start");
      if (endDate != null)
        throw new IllegalArgumentException("get-meetings by email, with floor or room, must not have --end");
      if (floor == null)
        throw new IllegalArgumentException("get-meetings by email, with room, must also have --floor");
      
      String tag = START_TIME_PREFIX + startDate + SPLIT + FLOOR_PREFIX + floor;
      if (room != null)
        tag += SPLIT + ROOM_PREFIX + room;

      keyExpr += " and begins_with(" + GSI1_SORT_KEY + ", :tag)";
      attrValues.put(":tag", AttributeValue.builder().s(tag).build());
    }
    String filterExpr = "begins_with(" + PARTITION_KEY + ",:pk)";

    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(keyExpr)
      .expressionAttributeValues(attrValues)
      .filterExpression(filterExpr);
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
  void AddValueWithFallback(HashMap<String, AttributeValue> attrValues, String name, String value, String prefix, String fallback)
  {
    if (value != null) {
      attrValues.put(name,
      AttributeValue.builder()
      .s(prefix + value) 
      .build());
    } else {
      attrValues.put(name,
      AttributeValue.builder()
      .s(fallback) 
      .build());
    }
  }

  public List<Meeting> getMeetingsById(String id, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":id", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX + id).build());
    AddValue(attrValues, ":startDate", startDate, START_TIME_PREFIX);
    AddValue(attrValues, ":endDate", endDate, START_TIME_PREFIX);
    String filterExpr = GetFilterForRange(startDate, endDate, ":startDate", ":endDate", SORT_KEY);
    
    QueryRequest.Builder builder = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY + " = :id")
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
  protected List<Timecard> TimecardFromResp(QueryResponse resp) {
    final ArrayList<Timecard> results = new ArrayList<Timecard>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Timecard.fromItem(item));
    }
    return results;
  }
  protected List<Reservation> ReservationFromResp(QueryResponse resp) {
    final ArrayList<Reservation> results = new ArrayList<Reservation>();
    for (Map<String,AttributeValue> item : resp.items()) {
      results.add(Reservation.fromItem(item));
    }
    return results;
  }

  static String MakeFilter(String pkStr)
  {
    return "begins_with(" + PARTITION_KEY + "," + pkStr + ")";
  }
  static String MakeFilter(String pkStr, String skStr)
  {
    return "begins_with(" + PARTITION_KEY + "," + pkStr + ") and begins_with(" + SORT_KEY + "," + skStr + ")";
  }
  
  protected List<Map<String,AttributeValue>> scanTable() {
    final ScanRequest request = ScanRequest.builder().tableName(tableName).build();
    final ScanResponse response = ddbClient.scan(request);
    return response.items();
  }

  protected List<Employee> ScanEmployees(String building, String floor, String room, String desk) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":e", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX).build());
    String filterExpr = MakeFilter(":e", ":e");
    if (building != null) {
      filterExpr += " and contains(SK3, :building)";
      attrValues.put(":building", AttributeValue.builder().s(BUILDING_PREFIX + building).build());
    }
    if (floor != null) {
      filterExpr += " and contains(SK3, :floor)";
      attrValues.put(":floor", AttributeValue.builder().s(FLOOR_PREFIX + floor).build());
    }
    if (room != null) {
      filterExpr += " and contains(SK3, :room)";
      attrValues.put(":room", AttributeValue.builder().s(ROOM_PREFIX + room).build());
    }
    if (desk != null) {
      filterExpr += " and contains(SK3, :desk)";
      attrValues.put(":desk", AttributeValue.builder().s(DESK_PREFIX + desk).build());
    }

    final ScanRequest request = ScanRequest.builder().tableName(tableName).
    filterExpression(filterExpr)
    .expressionAttributeValues(attrValues)
    .build();
    final ScanResponse response = ddbClient.scan(request);
    return response
      .items()
      .stream()
      .map(item -> Employee.fromItem(item))
      .sorted(Comparator.comparing(Employee::getEmployeeNumber))
      .collect(Collectors.toList());
  }

  protected List<Reservation> ScanReservations(String startDate, String endDate, String floor, String room) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":p", AttributeValue.builder().s(RESERVATION_PREFIX).build());
    String filterExpr = MakeFilter(":p");
    if (startDate != null) {
      filterExpr += " and " + START_TIME_NAME + " >= :startDate";
      attrValues.put(":startDate", AttributeValue.builder().s(startDate).build());
    }
    if (endDate != null) {
      filterExpr += " and " + START_TIME_NAME + " <= :endDate";
      attrValues.put(":endDate", AttributeValue.builder().s(endDate).build());
    }
    if (floor != null) {
      filterExpr += " and contains(" + GSI1_SORT_KEY + ", :floor)";
      attrValues.put(":floor", AttributeValue.builder().s(FLOOR_PREFIX + floor).build());
    }
    if (room != null) {
      filterExpr += " and contains(" + GSI1_SORT_KEY + ", :room)";
      attrValues.put(":room", AttributeValue.builder().s(ROOM_PREFIX + room).build());
    }

    final ScanRequest request = ScanRequest.builder().tableName(tableName).
      filterExpression(filterExpr)
      .expressionAttributeValues(attrValues)
      .build();
    final ScanResponse response = ddbClient.scan(request);
    final ArrayList<Reservation> results = new ArrayList<Reservation>();
    for (Map<String,AttributeValue> item : response.items()) {
      results.add(Reservation.fromItem(item));
    }
    return results;
  }

  protected List<Meeting> ScanMeetings(String startDate, String endDate, String floor, String room) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":e", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX).build());
    attrValues.put(":s", AttributeValue.builder().s(START_TIME_PREFIX).build());
    AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
    AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
    String filterExpr = MakeFilter(":e", ":s") + " and " + GSI1_SORT_KEY + " between :startDate and :endDate";
    if (floor != null) {
      filterExpr += " and contains(" + GSI1_SORT_KEY + ", :floor)";
      attrValues.put(":floor", AttributeValue.builder().s(FLOOR_PREFIX + floor).build());
    }
    if (room != null) {
      filterExpr += " and contains(" + GSI1_SORT_KEY + ", :room)";
      attrValues.put(":room", AttributeValue.builder().s(ROOM_PREFIX + room).build());
    }

    final ScanRequest request = ScanRequest.builder().tableName(tableName).
      filterExpression(filterExpr)
      .expressionAttributeValues(attrValues)
      .build();
    final ScanResponse response = ddbClient.scan(request);
    final ArrayList<Meeting> results = new ArrayList<Meeting>();
    for (Map<String,AttributeValue> item : response.items()) {
      results.add(Meeting.fromItem(item));
    }
    return results;
  }

  protected List<Project> ScanProjects(String startDate, String endDate, String startTarget, String endTarget) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":p", AttributeValue.builder().s(PROJECT_NAME_PREFIX).build());

    String filterExpr = MakeFilter(":p", ":p");
    if (startDate != null) {
      filterExpr += " and " + START_TIME_NAME + " >= :startDate";
      attrValues.put(":startDate", AttributeValue.builder().s(startDate).build());
    }
    if (endDate != null) {
      filterExpr += " and " + START_TIME_NAME + " <= :endDate";
      attrValues.put(":endDate", AttributeValue.builder().s(endDate).build());
    }

    if (startTarget != null) {
      filterExpr += " and " + TARGET_DATE_NAME + " >= :startTarget";
      attrValues.put(":startTarget", AttributeValue.builder().s(startTarget).build());
    }
    if (endTarget != null) {
      filterExpr += " and " + TARGET_DATE_NAME + " <= :endTarget";
      attrValues.put(":endTarget", AttributeValue.builder().s(endTarget).build());
    }

    final ScanRequest request = ScanRequest.builder().tableName(tableName).
      filterExpression(filterExpr)
      .expressionAttributeValues(attrValues)
      .build();
    final ScanResponse response = ddbClient.scan(request);
    final ArrayList<Project> results = new ArrayList<Project>();
    for (Map<String,AttributeValue> item : response.items()) {
      results.add(Project.fromItem(item));
    }
    return results;
  }

  protected List<Ticket> ScanTickets(String startDate, String endDate) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":t", AttributeValue.builder().s(TICKET_NUMBER_PREFIX).build());
    String filterExpr = MakeFilter(":t");
    if (startDate != null) {
      attrValues.put(":startDate", AttributeValue.builder().s(startDate).build());
      filterExpr += " and " + MODIFIED_DATE_NAME + " >= :startDate";
    }
    if (endDate != null) {
      attrValues.put(":endDate", AttributeValue.builder().s(endDate).build());
      filterExpr += " and " + MODIFIED_DATE_NAME + " <= :endDate";
    }
    final ScanRequest request = ScanRequest.builder().tableName(tableName)
      .filterExpression(filterExpr)
      .expressionAttributeValues(attrValues)
      .build();
    final ScanResponse response = ddbClient.scan(request);
    final ArrayList<Ticket> results = new ArrayList<Ticket>();
    for (Map<String,AttributeValue> item : response.items()) {
      results.add(Ticket.fromItem(item));
    }
    return results;
  }

  protected List<Timecard> ScanTimecards(String startDate, String endDate, String role) {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":p", AttributeValue.builder().s(PROJECT_NAME_PREFIX).build());
    attrValues.put(":s", AttributeValue.builder().s(START_TIME_PREFIX).build());
    String filterExpr = MakeFilter(":p", ":s");
    if (startDate != null) {
      attrValues.put(":startDate", AttributeValue.builder().s(startDate).build());
      filterExpr += " and " + START_TIME_NAME + " >= :startDate";
    }
    if (endDate != null) {
      attrValues.put(":endDate", AttributeValue.builder().s(endDate).build());
      filterExpr += " and " + START_TIME_NAME + " <= :endDate";
    }
    if (role != null) {
      attrValues.put(":role", AttributeValue.builder().s(role).build());
      filterExpr += " and " + ROLE_NAME + " = :role";
    }
    final ScanRequest request = ScanRequest.builder().tableName(tableName).
      filterExpression(MakeFilter(":p", ":s"))
      .expressionAttributeValues(attrValues)
      .build();
    final ScanResponse response = ddbClient.scan(request);
    final ArrayList<Timecard> results = new ArrayList<Timecard>();
    for (Map<String,AttributeValue> item : response.items()) {
      results.add(Timecard.fromItem(item));
    }
    return results;
  }

  public List<Employee> getEmployeeById(String id)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":id", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX + id).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY + " = :id and SK = :id")
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public List<Reservation> getReservationsById(String id)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s(RESERVATION_PREFIX + id).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY + " = :name and " + SORT_KEY + " = :name")
    .expressionAttributeValues(attrValues)
    .build();

    return ReservationFromResp(ddbClient.query(request));
  }

  public List<Reservation> getReservationsByEmail(String email, String startDate, String endDate, String floor, String room)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s(ORGANIZER_EMAIL_PREFIX + email).build());
    String sortExpr = null;
    if (floor != null || room != null) {
      String val = START_TIME_PREFIX + startDate;
      if (floor != null) val += SPLIT + FLOOR_PREFIX + floor;
      if (room != null) val += SPLIT + ROOM_PREFIX + room;
      attrValues.put(":val", AttributeValue.builder().s(val).build());
      sortExpr = "begins_with(" + GSI1_SORT_KEY + ", :val)";
    } else {
      AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
      AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
      sortExpr = GSI1_SORT_KEY + " between :startDate and :endDate";
    }
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI1_NAME)
    .keyConditionExpression(GSI1_PARTITION_KEY + " = :name and " + sortExpr)
    .expressionAttributeValues(attrValues)
    .build();

    return ReservationFromResp(ddbClient.query(request));
  }

  public List<Reservation> getReservationsByBuilding(String building, String startDate, String endDate, String floor, String room)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s(BUILDING_PREFIX + building).build());
    String sortExpr = null;
    if (floor != null || room != null) {
      String val = START_TIME_PREFIX + startDate;
      if (floor != null) val += SPLIT + FLOOR_PREFIX + floor;
      if (room != null) val += SPLIT + ROOM_PREFIX + room;
      attrValues.put(":val", AttributeValue.builder().s(val).build());
      sortExpr = "begins_with(" + GSI3_SORT_KEY + ", :val)";
    } else {
      AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
      AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
      sortExpr = GSI3_SORT_KEY + " between :startDate and :endDate";
    }

    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI3_NAME)
    .keyConditionExpression(GSI3_PARTITION_KEY + " = :name and " + sortExpr)
    .expressionAttributeValues(attrValues)
    .build();

    return ReservationFromResp(ddbClient.query(request));
  }

  public List<Timecard> getTimecardsByEmail(String email, String startDate, String endDate, String role)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s(EMPLOYEE_EMAIL_PREFIX + email).build());
    AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
    AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
    String filterExpr = null;
    attrValues.put(":tag", AttributeValue.builder().s(PROJECT_NAME_PREFIX).build());
    if (role != null) {
      attrValues.put(":role", AttributeValue.builder().s(role).build());
      filterExpr = ROLE_NAME + " = :role and begins_with(" + PARTITION_KEY + ", :tag)";
    } else {
      filterExpr = "begins_with(" + PARTITION_KEY + ", :tag)";
    }

    QueryRequest.Builder builder = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI1_NAME)
    .keyConditionExpression(GSI1_PARTITION_KEY + " = :email and " + GSI1_SORT_KEY + " between :startDate and :endDate")
    .expressionAttributeValues(attrValues)
    .filterExpression(filterExpr);
    return TimecardFromResp(ddbClient.query(builder.build()));
  }

  public List<Timecard> getTimecardsByName(String name, String startDate, String endDate, String role)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s(PROJECT_NAME_PREFIX + name).build());
    AddValueWithFallback(attrValues, ":startDate", startDate, START_TIME_PREFIX, START_TIME_PREFIX);
    AddValueWithFallback(attrValues, ":endDate", endDate, START_TIME_PREFIX, IncrString(START_TIME_PREFIX));
    String filterExpr = null;
    if (role != null) {
      attrValues.put(":role", AttributeValue.builder().s(role).build());
      filterExpr = ROLE_NAME + " = :role";
    }

    QueryRequest.Builder builder = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY + " = :name and " + SORT_KEY + " between :startDate and :endDate")
    .expressionAttributeValues(attrValues);
    if (filterExpr != null) builder = builder.filterExpression(filterExpr);
    return TimecardFromResp(ddbClient.query(builder.build()));
  }

  public List<Project> getProjectByName(String name)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":name", AttributeValue.builder().s(PROJECT_NAME_PREFIX + name).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .keyConditionExpression(PARTITION_KEY + " = :name and " + SORT_KEY + " = :name")
    .expressionAttributeValues(attrValues)
    .build();

    return ProjectFromResp(ddbClient.query(request));
  }

  public List<Project> getProjectsByStatus(String status, String startDate, String endDate, String startTarget, String endTarget)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":status", AttributeValue.builder().s(STATUS_PREFIX + status).build());
    AddValue(attrValues, ":startDate", startDate, START_TIME_PREFIX);
    AddValue(attrValues, ":endDate", endDate, START_TIME_PREFIX);
    AddValue(attrValues, ":startTarget", startTarget, "");
    AddValue(attrValues, ":endTarget", endTarget, "");
    String dateExpr = GetKeyExprForRange(startDate, endDate, GSI1_SORT_KEY);
    String filterExpr = GetFilterForRange(startTarget, endTarget, ":startTarget", ":endTarget", TARGET_DATE_NAME);

    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(GSI1_PARTITION_KEY + " = :status" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (filterExpr != null) builder = builder.filterExpression(filterExpr);
    return ProjectFromResp(ddbClient.query(builder.build()));
  }

  public List<Employee> getEmployeeByEmail(String email)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s(EMPLOYEE_EMAIL_PREFIX + email).build());
    attrValues.put(":e", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX).build());

    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI1_NAME)
    .keyConditionExpression(GSI1_PARTITION_KEY + " = :email and begins_with(" + GSI1_SORT_KEY + ", :e)")
    .expressionAttributeValues(attrValues)
    .build();

    return EmployeeFromResp(ddbClient.query(request));
  }

  public List<Employee> getEmployeeByManager(String email)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":email", AttributeValue.builder().s(MANAGER_EMAIL_PREFIX + email).build());
    attrValues.put(":e", AttributeValue.builder().s(EMPLOYEE_NUMBER_PREFIX).build());
    
    final QueryRequest request = QueryRequest.builder()
    .tableName(tableName)
    .indexName(GSI2_NAME)
    .keyConditionExpression(GSI2_PARTITION_KEY + " = :email and begins_with(SK, :e)")
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
    locTag = AppendStrWithPrefix(locTag, building, BUILDING_PREFIX);
    locTag = AppendStrWithPrefix(locTag, floor, FLOOR_PREFIX);
    locTag = AppendStrWithPrefix(locTag, room, ROOM_PREFIX);
    locTag = AppendStrWithPrefix(locTag, desk, DESK_PREFIX);

    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":city", AttributeValue.builder().s(CITY_PREFIX + city).build());
    
    String keyExpr;
    if (locTag.isEmpty()) {
      keyExpr = GSI3_PARTITION_KEY + " = :city";
    } else {
      keyExpr = GSI3_PARTITION_KEY + " = :city and begins_with(SK3, :loc)";
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
    attrValues.put(":ticket", AttributeValue.builder().s(TICKET_NUMBER_PREFIX + ticket).build());
    AddValue(attrValues, ":startDate", startDate, MODIFIED_DATE_PREFIX);
    AddValue(attrValues, ":endDate", endDate, MODIFIED_DATE_PREFIX);
    String dateExpr = GetKeyExprForRange(startDate, endDate, SORT_KEY);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .keyConditionExpression(PARTITION_KEY + " = :ticket" + dateExpr)
      .expressionAttributeValues(attrValues);
    return TicketFromResp(ddbClient.query(builder.build()));
  }

  public List<Ticket> getTicketByAuthor(String author, String ticket, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":author", AttributeValue.builder().s(AUTHOR_EMAIL_PREFIX + author).build());
    AddValue(attrValues, ":startDate", startDate, MODIFIED_DATE_PREFIX);
    AddValue(attrValues, ":endDate", endDate, MODIFIED_DATE_PREFIX);
    AddValue(attrValues, ":ticket", ticket, TICKET_NUMBER_PREFIX);
    String dateExpr = GetKeyExprForRange(startDate, endDate, GSI1_SORT_KEY);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI1_NAME)
      .keyConditionExpression(GSI1_PARTITION_KEY + " = :author" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (ticket != null) builder = builder.filterExpression(PARTITION_KEY + " = :ticket");
    return TicketFromResp(ddbClient.query(builder.build()));
  }

  public List<Ticket> getTicketByAssignee(String assignee, String ticket, String startDate, String endDate)
  {
    HashMap<String, AttributeValue> attrValues = new HashMap<>();
    attrValues.put(":assignee", AttributeValue.builder().s(ASSIGNEE_EMAIL_PREFIX + assignee).build());
    AddValue(attrValues, ":startDate", startDate, MODIFIED_DATE_PREFIX);
    AddValue(attrValues, ":endDate", endDate, MODIFIED_DATE_PREFIX);
    AddValue(attrValues, ":ticket", ticket, TICKET_NUMBER_PREFIX);
    String dateExpr = GetKeyExprForRange(startDate, endDate, SORT_KEY);
    
    QueryRequest.Builder builder = QueryRequest.builder()
      .tableName(tableName)
      .indexName(GSI2_NAME)
      .keyConditionExpression(GSI2_PARTITION_KEY + " = :assignee" + dateExpr)
      .expressionAttributeValues(attrValues);
    if (ticket != null) builder = builder.filterExpression(PARTITION_KEY + " = :ticket");
    return TicketFromResp(ddbClient.query(builder.build()));
  }
}

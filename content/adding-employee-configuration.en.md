---
title : "Exercise 3"
weight : 300
---

<!-- !test program
./utils/check-block.sh ./exercises/java/exercise-3 <&0
 -->

# Exercise 3: Adding The Employee Record access patterns

In this section, you will will configure searchable encryption
to configure the access patterns for the Employee data type.

## Background

In Exercise 2, you configured [searchable encryption](TODO)
to enable you to perform some simple searches on Timecard records.

In this exercise, you will add support for Employee records,
which we will need to search in much more complex ways.

As you configure each new beacon to support a new access pattern,
consider what [truncation length is appropriate for that beacon](TODO)
as well as whether [beacons are right for that access pattern in the first place](TODO).
As you go through the workshop, note that various
considerations on the tradeoffs being made between security and performance.

## Let's Go!

### Starting Directory

If you just finished [Adding the Database Encryption SDK](./adding-the-database-encryption-sdk.md), you are all set.

If you aren't sure, or want to catch up,
jump into the `adding-searchable-encryption-configuration` directory for the language of your choice.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/exercises/java/adding-searchable-encryption-configuration-start
```



:::
::::

### Step 1:

Let's begin by making a new name for our table.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1 -->
```java
    // BEGIN EXERCISE 3 STEP 1
    public static final String TABLE_NAME = "Exercise3_Table";
    // END EXERCISE 3 STEP 1
```

:::
::::


### Step 2:

We will need several more Standard Beacons
in order to search Employee Records.
These Standard Beacons will be referenced by Compound Beacons in later steps.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 2 -->
```java
    // BEGIN EXERCISE 3 STEP 2
    beacons.add(StandardBeacon.builder()
        .name(MANAGER_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(BUILDING_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + BUILDING_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(CITY_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + CITY_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(FLOOR_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + FLOOR_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ROOM_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + ROOM_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(DESK_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + DESK_NAME)
        .build());
    // END EXERCISE 3 STEP 2
```

:::
::::

#### What Happened?

Six more Standard Beacons are now available for use with Compound Beacons.

Five of these reach inside of the "locations" map, and make a 
searchable beacon from a single field of the map.

### Step 3:

For Employee Records, the Partition Key for GSI1 will be employeeName.
As Timecard also used employeeName for the Partition key, we need add nothing more here.

For Employee Records, the Sort Key for GSI1 will be employeeNumber.
We will not be doing ranged searches on employeeNumber;
instead, this is just to disambiguate Employee Records from Timecard Records
that have the same Partition key but a different Sort key.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3 -->
```java
    // BEGIN EXERCISE 3 STEP 3
    signedParts.add(SignedPart.builder().name(EMPLOYEE_NUMBER_NAME).prefix(EMPLOYEE_NUMBER_PREFIX).build());
    // END EXERCISE 3 STEP 3
```

:::
::::

#### What Happened?

employeeNumber is now available as a Part for the GSI1 Partition key compound beacon.

### Step 4a:

In this step we add routines to build the Constructors for the index keys
for all the indexes for Employee Records.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4a -->
```java
  // BEGIN EXERCISE 3 STEP 4a

  public static Constructor MakeGsi1EmployeeSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(EMPLOYEE_NUMBER_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi2EmployeePartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(MANAGER_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi3EmployeePartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(CITY_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi3EmployeeSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(BUILDING_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(FLOOR_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(ROOM_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(DESK_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  // END EXERCISE 3 STEP 4a
```

#### What Happened?

We now have Compound Beacon Constructors for the partition keys of all three GSIs,
plus the sort key for GSI3.

### Step 4a:

In this step we write the routines to create the Compound Beacons
for the keys for GSI2 and GSI3.

<!-- !test check java step 4b -->
```java
  // BEGIN EXERCISE 3 STEP 4b

  public static CompoundBeacon MakeGsi2PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(MANAGER_EMAIL_NAME).prefix(MANAGER_EMAIL_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi2EmployeePartitionKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI2_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi3PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(CITY_NAME).prefix(CITY_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi3EmployeePartitionKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI3_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi3SortKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(BUILDING_NAME).prefix(BUILDING_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(FLOOR_NAME).prefix(FLOOR_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ROOM_NAME).prefix(ROOM_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(DESK_NAME).prefix(DESK_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi3EmployeeSortKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI3_SORT_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  // END EXERCISE 3 STEP 4b
```

:::
::::

#### What Happened?

We can now build the Compound Beacons for the partition keys of GSI2 and GSI3,
plus the Sort key for GSI3.

### Step 5:

For GSI1's Sort key, we need to add the new Constructor.

Recall that for Employee Records, the Partition key for GSI1 is the same as for Timecard Records,
so nothing needs to be added for that.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5 -->
```java
    // BEGIN EXERCISE 3 STEP 5
    constructors.add(MakeGsi1EmployeeSortKeyConstructor());
    // END EXERCISE 3 STEP 5
```

:::
::::

#### What Happened?

GSI1's Sort Key is now ready to go.


### Step 6:

Now we add our new Compound Beacons to the list.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 6 -->
```java
    // BEGIN EXERCISE 3 STEP 6
    beacons.add(MakeGsi2PartitionKey());
    beacons.add(MakeGsi3PartitionKey());
    beacons.add(MakeGsi3SortKey());
    // END EXERCISE 3 STEP 6
```

:::
::::

#### What Happened?

We have completed all of the beacon configuration for Employee Records.

### Step 7:

When an index key is a compound beacon with encrypted parts,
you have to specify "aws_dbe_b_foo" where you might expect to put "foo".

This is because it is legal to have both a regular attribute "foo" and a beacon named "foo",
and so the beacon is stored under the name "aws_dbe_b_foo".

Thus when we create the table, and therefore the GSIs, we must specify this new name.

<!-- !test check java step 7a -->
```java
    // BEGIN EXERCISE 3 STEP 7a
      .attributeName(BEACON_PREFIX + GSI2_PARTITION_KEY)
    // END EXERCISE 3 STEP 7a
```

<!-- !test check java step 7b -->
```java
    // BEGIN EXERCISE 3 STEP 7b
    .attributeName(BEACON_PREFIX + GSI3_PARTITION_KEY)
    // END EXERCISE 3 STEP 7b
```

<!-- !test check java step 7c -->
```java
    // BEGIN EXERCISE 3 STEP 7c
    .attributeName(BEACON_PREFIX + GSI3_SORT_KEY)
    // END EXERCISE 3 STEP 7c
```

<!-- !test check java step 7d -->
```java
    // BEGIN EXERCISE 3 STEP 7d
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI2_PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI3_PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(BEACON_PREFIX + GSI3_SORT_KEY)
      .attributeType(ScalarAttributeType.S).build());
    // END EXERCISE 3 STEP 7d
```

#### What Happened?

The GSIs now all point to the appropriate beacon.

We are now finished with the configuration changes for the Employee Records.

Nothing needs to change in the code that reads, writes or queries.


## Try it Out

Now that you have written the code,
let's try it out and see what it does.

You likely already did this,
but if you don't already have a branch key
in your `config.toml` run this
and add the branch key id.


<!-- !test program
BRANCH_KEY_ID=$(./exercises/java/exercise-3/employee-portal create-branch-key -l | tail -n 1 | sed 's/.*: //' | sed 's/^/\\\"/; s/$/\\\"/')
./utils/sed-add-change.sh "branch_key_id.*" "branch_key_id = $BRANCH_KEY_ID" ./exercises/config.toml
 -->

<!-- !test check create-branch-key -->
```bash
./employee-portal create-branch-key
```

First, let's create the table that will back the Employee Portal Service.
We have made this easy for you by providing a target within the CLI.

<!-- !test program
# This is dangerous because `eval` lets you do anything.
# However if you have access to modify the code block
# then you could modify this script...

read command_input
cd ./exercises/java/exercise-3
eval "$command_input" -l
 -->

<!-- !test check create-table -->
```bash
./employee-portal create-table
```

[Go to the DynamoDB AWS Console to confirm that your expected table is created](TODO).

Next, load up some test data into your portal!
We have provided a script that puts some sample data into your table.

<!-- !test check load-data -->
```bash
./load-data
```

Similar to how we got and put records into the plaintext Employee Portal Service,
you can use the CLI to retrieve and put records into our Employee Portal Service
with client-side encryption.

### Retrieve items from your encrypted table

To start, let's retrieve all of our employees again:

```bash
./employee-portal get-employees
```

This shows all of the employee records.

To test GSI1, try

```bash
./employee-portal get-employees --employee-number=XXX
```

To test GSI2, try

```bash
./employee-portal get-employees --manager-email=XXX
```

To test GSI3, try

```bash
./employee-portal get-employees --city=XXX
```

# Next exercise

Now that you have added support for Timecards (simple) and Employees (complex)
we need to repeat the process for the four remaining record types.

Move onto the next exercise:
[Adding a searchable encryption configuration](./adding-the-remaining-access-patterns.en.md).

---
title : "Exercise 4"
weight : 400
---

<!-- !test program
./utils/check-block.sh ./workshop/java/exercise-4 <&0
 -->

# Exercise 4: Adding The Remaining Access Patterns

In this section, you will learn how to configure searchable encryption for remaining data in the sample database.

## Background

In Exercise 3, you configured [searchable encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html)
to enable you to perform queries on Employee Records.

In this exercise, you will add support for the remaining record types.

As you configure each new beacon to support a new access pattern,
consider what [beacon length is appropriate for that beacon](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-length.html)
as well as whether [beacons are right for that access pattern in the first place](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html#are-beacons-right-for-me).

## Let's Go!

### Starting Directory

If you just finished [Adding The Employee Record access patterns](../exercise-3),
you are all set.

If you aren't sure, or want to catch up,
jump into the `exercise-4` directory for the language of your choice.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/java/exercise-4
```

:::
::::

### Step 1:

As always, this exercise will take place in its own table.
Update your Config to use a new table name for this exercise.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-4/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1 -->
```java
    // BEGIN EXERCISE 4 STEP 1
    public static final String TABLE_NAME = "Exercise4_Table";
    // END EXERCISE 4 STEP 1
```

:::
::::

#### What Happened?

TABLE_NAME has been updated so that this exercise will work in a separate table.

### Step 2:

We need to associate beacons with five more encrypted attributes.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-4/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

Configure standard beacons for status, organizer email, assignee email,
severity, and role.

For now, use a beacon length of 8.
Note that this is just for demonstrative purposes.
Because this workshop only deals with a handful of sample
data for each item type (employee, ticket, etc),
we don't expect this beacon length to result in
false positives during queries.
Before using beacons for your own use case,
refer to [our documentation on beacon length](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-length.html).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 2 -->
```java
    // BEGIN EXERCISE 4 STEP 2
    beacons.add(StandardBeacon.builder()
        .name(STATUS_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ORGANIZER_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ASSIGNEE_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(AUTHOR_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(SEVERITY_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ROLE_NAME)
        .length(8)
        .build());
    // END EXERCISE 4 STEP 2
```

:::
::::

#### What Happened?

You have configured standard beacons over all of the encrypted values
in your table you want to search on.
In the next step, you will be able to use these standard beacons
to add to your pre-existing compound beacons configurations.

### Step 3:

As we did for Timecards and Employees, each of the remaining four record types
needs Compound Beacons constructors to satisfy their individual access patterns.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3 -->
```java
  // BEGIN EXERCISE 4 STEP 3
  public static Constructor MakeGsi1MeetingSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(START_TIME_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(FLOOR_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(ROOM_NAME).required(false).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi1ProjectPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(STATUS_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi1ReservationPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(ORGANIZER_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi3ReservationPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(BUILDING_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi1TicketPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(AUTHOR_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi1TicketSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(MODIFIED_DATE_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi2TicketPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(ASSIGNEE_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  public static Constructor MakeGsi3TicketPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(SEVERITY_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  // END EXERCISE 4 STEP 3
```

:::
::::

#### What Happened?

You have created constructors for each of the access patterns you need to support.
In the next step, you will add these constructors into the configuration of your
compound beacons.

### Step 4:

Ticket records use GSI2 with the ASSIGNEE_EMAIL_NAME attribute,
and the usual custom constructor.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4a -->
```java
    // BEGIN EXERCISE 4 STEP 4a
    encryptedParts.add(EncryptedPart.builder().name(ASSIGNEE_EMAIL_NAME).prefix(ASSIGNEE_EMAIL_PREFIX).build());
    // END EXERCISE 4 STEP 4a
```

<!-- !test check java step 4b -->
```java
    // BEGIN EXERCISE 4 STEP 4b
    constructors.add(MakeGsi2TicketPartitionKeyConstructor());
    // END EXERCISE 4 STEP 4b
```

:::
::::

#### What Happened?

You have configured your compound beacon for GSI2 partition key to write
the beacon to use the partition constructor for tickets,
writing the beacon for assignee email if it exists on the item.

### Step 5:

Tickets and Reservations use the GSI3 Partition Key.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5a -->
```java
    // BEGIN EXERCISE 4 STEP 5a
    encryptedParts.add(EncryptedPart.builder().name(BUILDING_NAME).prefix(BUILDING_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(SEVERITY_NAME).prefix(SEVERITY_PREFIX).build());
    // END EXERCISE 4 STEP 5a
```

<!-- !test check java step 5b -->
```java
    // BEGIN EXERCISE 4 STEP 5b
    constructors.add(MakeGsi3ReservationPartitionKeyConstructor());
    constructors.add(MakeGsi3TicketPartitionKeyConstructor());
    // END EXERCISE 4 STEP 5b
```

:::
::::

#### What Happened?

You have configured your compound beacon for the GSI3 partition key to:
- if no other constructors are used, write the partition key for reservations if the item contains a building ID
- otherwise, write the partition key for tickets if the item contain a severity

### Step 6:

Similarly, Tickets and Reservations use the GSI3 Sort Key.


::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 6a -->
```java
    // BEGIN EXERCISE 4 STEP 6a
    ArrayList<SignedPart> signedParts = new ArrayList<SignedPart>();
    signedParts.add(SignedPart.builder().name(MODIFIED_DATE_NAME).prefix(MODIFIED_DATE_PREFIX).build());
    signedParts.add(SignedPart.builder().name(START_TIME_NAME).prefix(START_TIME_PREFIX).build());
    // END EXERCISE 4 STEP 6a
```

<!-- !test check java step 6b -->
```java
    // BEGIN EXERCISE 4 STEP 6b
    constructors.add(MakeGsi1TicketSortKeyConstructor()); // GSI3 same as GSI1
    constructors.add(MakeGsi1MeetingSortKeyConstructor()); // reservation GSI3 same as Meeting GSI1
    // END EXERCISE 4 STEP 6b
```

<!-- !test check java step 6c -->
```java
    // BEGIN EXERCISE 4 STEP 6c
        .signed(signedParts)
    // END EXERCISE 4 STEP 6c
```

:::
::::

#### What Happened?

You have configured your compound beacon for the GSI3 sort key to:
- if no other constructors are used, write the beacon for modify date if it exists on the item
- otherwise, write the start time, floor, and room if those attributes exist on the item

### Step 7:

Projects, Tickets and Reservations need their own constructors for the GSI1 Partition Key.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 7a -->
```java
    // BEGIN EXERCISE 4 STEP 7a
    encryptedParts.add(EncryptedPart.builder().name(STATUS_NAME).prefix(STATUS_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ORGANIZER_EMAIL_NAME).prefix(ORGANIZER_EMAIL_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(AUTHOR_EMAIL_NAME).prefix(AUTHOR_EMAIL_PREFIX).build());
    // END EXERCISE 4 STEP 7a
```

<!-- !test check java step 7b -->
```java
    // BEGIN EXERCISE 4 STEP 7b
    constructors.add(MakeGsi1ProjectPartitionKeyConstructor());
    constructors.add(MakeGsi1ReservationPartitionKeyConstructor());
    constructors.add(MakeGsi1TicketPartitionKeyConstructor());
    // END EXERCISE 4 STEP 7b
```

:::
::::

#### What Happened?

You have configured your compound beacon for the GSI1 partition key to:
- if no other constructors are used, write the beacon for status if it exists on the item
- otherwise, write the beacon for organizer email if it exists on the item
- otherwise, write the author email if it exists on the item

### Step 8:

Tickets and Reservations need their own constructors for the GSI1 Sort Key.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 8a -->
```java
    // BEGIN EXERCISE 4 STEP 8a
    signedParts.add(SignedPart.builder().name(MODIFIED_DATE_NAME).prefix(MODIFIED_DATE_PREFIX).build());

    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(FLOOR_NAME).prefix(FLOOR_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ROOM_NAME).prefix(ROOM_PREFIX).build());
    // END EXERCISE 4 STEP 8a
```

<!-- !test check java step 8b -->
```java
    // BEGIN EXERCISE 4 STEP 8b
    constructors.add(MakeGsi1MeetingSortKeyConstructor());
    // ReservationSortKeyConstructor is the same as MeetingSortKeyConstructor
    constructors.add(MakeGsi1TicketSortKeyConstructor());
    // END EXERCISE 4 STEP 8b
```

<!-- !test check java step 8c -->
```java
    // BEGIN EXERCISE 4 STEP 8c
        .signed(signedParts)
    // BEGIN EXERCISE 4 STEP 8c
```

:::
::::

#### What Happened?

You have configured your compound beacon for the GSI1 sort key to:
- if no other constructors are used, write the start time, floor, and room to the item if those attributes exist
- otherwise, write the beacon for modified date if it exists on the item

### Step 9:

The Sort Key for GSI1 now has some encrypted parts, where it didn't in exercise 3.

This means that the name of the attribute has changed from "SK1" to "aws_dbe_b_SK1".

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-4/src/main/java/sfw/example/dbesdkworkshop/Api.java`.

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 9a -->
```java
    // BEGIN EXERCISE 4 STEP 9a
    .attributeName(BEACON_PREFIX + GSI1_SORT_KEY)
    // END EXERCISE 4 STEP 9a
```

<!-- !test check java step 9b -->
```java
    // BEGIN EXERCISE 4 STEP 9b
      .attributeName(BEACON_PREFIX + GSI1_SORT_KEY)
    // END EXERCISE 4 STEP 9b
```

:::
::::

#### What Happened?

You have updated the configuration so that the table you create
will have a sort key for GSI1 with the correct name for use with beacons.

We have now added the full configuration for all record types.

All access patterns used in the original plaintext database work in the encrypted database,
and no change was required in any of the code involved in reading, writing or querying records;
only configuration, and index creation.


### Checking Your Work

If you want to check your progress, or compare what you've done versus a finished example,
look at the `complete` folder which contains a complete solution for this workshop.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```
Take a peek at `~/environment/workshop/java/complete`
```

:::
::::

## Try it Out

Now that you have completed configuring searchable configuration for each of your access
patterns, you should now be able to perform any query that was possible in your plaintext
Employee Portal Service in your client-side encrypted Employee Portal Service.

Again, since each exercise is independent,
you need to create the table.

<!-- !test program
cd ./workshop/java/exercise-4

# This is dangerous because `eval` lets you do anything.
# However if you have access to modify the code block
# then you could modify this script...
read command_input
if [[ -n "$USE_DDB_LOCAL" ]]; then
  eval "$command_input -l"
else
  eval "$command_input"
fi
 -->

<!-- !test check create-table -->
```bash
./employee-portal create-table
```

[Go to the DynamoDB AWS Console to confirm that your expected table is created](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise4_Table).
(If you are in the 'complete' folder, look for a table named 'Complete_Table'.)

Next, load up some test data into your portal!
We have provided a script that puts some sample data into your table.

<!-- !test check load-data -->
```bash
./load-data
```

If you take a look at your DynamoDB table, you will now see
a new attribute for every new beacon you configured.

### Try out the new access patterns

In Exercise 3, you were able to get employees by employee ID,
manager email, and city.

Now that you have completed creating beacons for each of our desired
access patterns, you can use our CLI to test even more access patterns out.

Let's try getting tickets in a variety of ways.
To start, let's retrieve all of our tickets
<!-- !test in get-tickets -->
```bash
./employee-portal get-tickets
```

You should see the same tickets that you were able to retrieve before
<!-- !test out get-tickets -->
```

WARNING : You are doing a full table scan. In real life, this would be very time consuming.

ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
2                   2022-10-06T14:32:25      zorro@gmail.com     charlie@gmail.com   3           Easy Bug            This seems simple enough
2                   2022-10-08T14:32:25      charlie@gmail.com   able@gmail.com      3           Easy Bug            that's in able's code
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
1                   2022-10-07T15:32:25      able@gmail.com      charlie@gmail.com   3           Bad Bug             Charlie should handle this
```

This shows all of the ticket records.

You configured your searchable encryption to create beacons over author email and modified date
and write those beacon values to the GSI1 partition key and GSI1 sort key respectively.

The CLI is configured to make use of GSI1's partition key if you perform the following command:
<!-- !test in get-tickets-email -->
```bash
./employee-portal get-tickets --assignee-email able@gmail.com
```
You should get back:
<!-- !test out get-tickets-email -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
2                   2022-10-08T14:32:25      charlie@gmail.com   able@gmail.com      3           Easy Bug            that's in able's code
```

The CLI is also configured to make use of the GSI1's sort key if you provide the right values.
Narrow down the results from our query further by specifying a time range:
<!-- !test in get-tickets-email-date -->
```bash
./employee-portal get-tickets --assignee-email able@gmail.com --start 2022-10-07T14:32:25 --end 2022-10-07T16:32:25
```
You should get back:
<!-- !test out get-tickets-email-date -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
```

You configured a different access pattern on tickets using GSI2.
For GSI2, you configured searchable encryption to write beacon values for assignee email
into GSI2's partition key.

To exercise a query over GSI2, try to get all the tickets asigned to a particular employee
<!-- !test in get-tickets --assignee-email able@gmail.com -->
```bash
./employee-portal get-tickets --assignee-email able@gmail.com
```
and you should get
<!-- !test out get-tickets --assignee-email able@gmail.com -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
2                   2022-10-08T14:32:25      charlie@gmail.com   able@gmail.com      3           Easy Bug            that's in able's code
```

Finally, you configured a third access pattern for tickets which utilizes GSI3.
For GSI3, you configured searchable encryption to write beacon values
for severity and modified date.

However, the CLI isn't currently wired up to construct queries using ticket severity
as an input.

Because you always need to specify a value for your partition keys when querying,
in the current state, if you specify a date range and nothing else,
the CLI will perform a scan over your table.
For example, if you run
<!-- !test in get-tickets --start 2022-10-07T14:32:25 --end 2022-10-07T16:32:25 -->
```bash
./employee-portal get-tickets --start 2022-10-07T14:32:25 --end 2022-10-07T16:32:25
```
you should get
<!-- !test out get-tickets --start 2022-10-07T14:32:25 --end 2022-10-07T16:32:25 -->
```

WARNING : You are doing a full table scan. In real life, this would be very time consuming.

ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
1                   2022-10-07T15:32:25      able@gmail.com      charlie@gmail.com   3           Bad Bug             Charlie should handle this
```

As an extra exercise, take a look at the CLI implementation in `Api.java`
and consider how you might expand it to allow you to make full
use out of GSI3 to query on tickets.

The full list of access patterns your table should support is listed below.
Try out a couple of these access patterns yourself:
1. Get meetings by date and email
1. Get meetings by date and employeeID
1. Get meetings by date and building/floor/room
1. Get employee data by email
1. Get meetings by email
1. Get tickets by email
1. Get reservations by email
1. Get time cards by email
1. Get employee info by employeeID
1. Get employee info by email
1. Get ticket history by ticket ID
1. Get ticket history by employee email
1. Get ticket history by assignee email
1. Get employees by city.building.floor.desk
1. Get employees by manager email
1. Get assigned tickets by assignee email
1. Get tickets last touched in the past 24 hours
1. Get projects by status, start and target date
1. Get projects by name
1. Get project history by date range
1. Get project history by role
1. Get reservations by building ID
1. Get reservations by building ID and time range

## Explore Further

Now that you know how to build an application that can use client-side encryption
with DynamoDB and still support complex access patterns,
let's take a closer look at a subtle property of searchable encryption
in the AWS Database Encryption SDK.

For demonstrative purposes this workshop has used example data that
does not meet our recommendations for [uniformity and non-correlation](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html#are-beacons-right-for-me).
Additionally, the sample code has chosen a beacon length
that does not meet our [recommendations](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-length.html)
because the workshop only ever works with a small amount of sample data.

However, by tweaking your code in a small way you can see how
beacon length can affect the security and performance of your data.

Go to the [AWS Console for your DynamoDB table](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise4_Table),
and take a look at your data.

Find the `aws_dbe_b_employeeEmail` attribute in your data, and compare the value against various items.
With a beacon output length of eight bits, the beacon can be one of 256 possible values.
However, our table only holds four different employee emails right now.
A beacon output length of eight means that it is likely that each of these four emails has
a distinct beacon value.
This reveals information that may not be acceptable to our threat model.

Let's see what happens if we chose a different beacon output length.

First, once you choose a beacon output length, you cannot update it.
Thus, to test out this change you will need to re-build your table.

Run the following:
```bash
./employee-portal delete-table
```

Now update your code that configures standard beacons to use
a `length` of `1` for each standard beacon instead of `8`.

For example:
::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```
    beacons.add(StandardBeacon.builder()
        .name(EMPLOYEE_EMAIL_NAME)
        .length(1) // Update here
        .build());
```

:::
::::


Recreate your table and reload the sample data:
```bash
./employee-portal create-table
./load-data
```

Take another look at the [AWS Console for your DynamoDB table](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise4_Table).

Because this beacon is created from standard beacons with an output length of one bit,
there are only two possible values the beacons can be.

Our sample data contains employees with four distinct employee ids.
Because there are only two beacon values that can be written, we expect that when
a beacon is written for employee id, some of those employee ids will need to share
a beacon value.

These are the "false positives" that can provide security to your searchable encrypted data.
While they provide security, these false positives come at the cost of performance.
For each false positive that gets returned alongside your expected data,
your application does extra work to decrypt and filter out these false positives.

Read our docs for more information on how to [choose the right beacon length](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-length.html)
for your dataset.

# Next exercise

Congratulations! You have officially completed the Busy Engineer's Document Bucket workshop.
Proceed to [Clean Up and Closing](../clean-up-and-closing) to tear down your workshop environment.

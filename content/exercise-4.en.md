---
title : "Exercise 4"
weight : 400
---


./utils/check-block.sh ./workshop/java/exercise-4 <&0
 -->

# Exercise 4: Adding The Remaining Access Patterns

In this section, you will will configure searchable encryption
to configure the remaining access patterns.

## Background

In Exercise 3, you configured [searchable encryption](TODO)
to enable you to perform queries on Employee Records.

In this exercise, you will add support for the remaining record types.

As you configure each new beacon to support a new access pattern,
consider what [truncation length is appropriate for that beacon](TODO)
as well as whether [beacons are right that access pattern in the first place](TODO).
As you go through the workshop, note that various
considerations on the tradeoffs being made between security and performance.

## Let's Go!

### Starting Directory

If you just finished [Adding The Employee Record access patterns](./exercise-3),
you are all set.

If you aren't sure, or want to catch up,
jump into the `exercise-3` directory for the language of your choice.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/java/exercise-4
```

:::
::::

### Step 1:

As always, this exercise will take place in its own table.

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


### Step 2:

We need to associate beacons with five more encrypted attributes.

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
    // END EXERCISE 4 STEP 2
```

:::
::::

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

We have now added the full configuration for all record types.

All access patterns used in the original plaintext database work in the encrypted database,
and no change was required in any of the code involved in reading, writing or querying records;
only configuration, and index creation.


### Checking Your Work

If you want to check your progress, or compare what you've done versus a finished example, check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/workshop/java/adding-the-remaining-access-patterns-complete
```

:::
::::

## Try it Out

Now that you have completed configuring searchable configuration for each of your access
patterns, you should now be able to perform any query that was possible in your plaintext
Employee Portal Service in your client-side encrypted Employee Portal Service.

For example, we can now [TODO]:

```bash
TODO compare
```

If you take a look at your DynamoDB table, you will now see
a new attribute for every new beacon you configured.
[TODO describe a couple in more detail]

Try out each of these access patterns yourself:
- TODO

## Explore Further

- TODO something more about beacon length

# Next exercise

Congratulations! You have officially completed the Busy Engineer's Document Bucket workshop.
Proceed to [Clean Up and Closing](../clean-up-and-closing) to tear down your workshop environment.

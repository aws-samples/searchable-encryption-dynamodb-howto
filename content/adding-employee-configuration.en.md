---
title : "Exercise 3"
weight : 300
---

<!-- !test program
./utils/check-block.sh ./exercises/java/exercise-3 <&0
 -->

# Exercise 3: 

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

### Step 3:

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

### Step 4:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4 -->
```java
  // BEGIN EXERCISE 3 STEP 4

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

  // END EXERCISE 3 STEP 4
```

:::
::::

### Step 5:

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

### Step 6:

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



## Try it Out





# Next exercise

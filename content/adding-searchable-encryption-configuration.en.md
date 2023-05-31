---
title : "Exercise 2"
weight : 200
---

<!-- !test program
./utils/check-block.sh ./exercises/java/exercise-2 <&0
 -->

# Exercise 2: Adding Searchable Encryption Configuration

In this section, you will configure the AWS Database Encryption SDK to use searchable encryption.

## Background

Your Employee Portal will encrypt items locally before you put them
into your table,
and will decrypt locally when you get items back from your table.

This presents us with an interesting problem.
If your table only ever sees your data in encrypted form,
how can you ever effectively query on that encrypted data?

To accomplish this, the AWS Database Encryption SDK for DynamoDB
includes the [searchable encryption](TODO) feature,
which allows you to calculate and store
[beacons](TODO) alongside your data.
The client then utilizes these beacons whenever you make a query,
retrieving the correct encrypted data for your query.

The example used in this workshop with searchable encryption
is for demonstrative purposes only.
Because the data in this example is non-uniform
and highly correlated, there is information that will
be leaked if we configure searchable encryption.
We continue with this example, however,
because it is a use-case with a wide variety of 
complex, yet still easy to understand, access-patterns.
Before you use [searchable encryption](TODO)
for your own use case, please read our [AWS documentation](TODO)
and ensure that [beacons are right for your use case](TODO).

To use [searchable encryption](TODO),
you need another kind of key: a beacon key.
This beacon key will be used during both write and query
in order to calculate the truncated HMACs that comprise beacons.

In [Exercise 2](TODO), you created a Key Store with a branch key.
The `CreateBranchKey` API additionally created a second key for
you that is associated with this branch key.
This second key, which we will call the beacon key,
will be the key responsible for generating beacons
alongside your encrypted data. 

By configuring searchable encryption with this beacon key,
you will be able to enable queries over your encrypted attributes.
In this exercise you will explore one access pattern in detail.

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

### Step 1: Create your table

First, create the DynamoDB table that will contain the data for your
Employee Portal Service.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

:::
::::

Update the table name to something specific to this exercise.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1 -->
```java
    // BEGIN EXERCISE 2 STEP 1
    public static final String TABLE_NAME = "Exercise2_Table";
    // END EXERCISE 2 STEP 1
```

:::
::::

Next, in order to support searchable encryption
for this access pattern,
the table you create will need to be updated to
include a new Global Secondary Index that uses the
beacon attribute.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Api.java`.

:::
::::

Update the CLI code that creates the table
so that it also creates the necessary Global
Secondary Index.

Instead of creating a GSI over "PK1" as in your
original Employee Portal Service,
you will now create a GSI over "aws_dbe_b_PK1".
"aws_dbe_b_" is a prefix that the AWS Database Encryption SDK
appends to the attribute names that hold beacon values.

Update the code that defines the first Global Secondary Index:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1 -->
```java
    final KeySchemaElement pk1Schema = KeySchemaElement
      .builder()
      // BEGIN EXERCISE 2 STEP 1
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
      // END EXERCISE 2 STEP 1
      .keyType(KeyType.HASH)
      .build();
```

:::
::::

In the same file, update the code
so that this new attribute is included
in the attribute definitions.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1 -->
```java
    final ArrayList<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>();
    attrs.add(AttributeDefinition.builder()
      .attributeName(PARTITION_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      .attributeName(SORT_KEY)
      .attributeType(ScalarAttributeType.S).build());
    attrs.add(AttributeDefinition.builder()
      // BEGIN EXERCISE 2 STEP 1
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
      // END EXERCISE 2 STEP 1
```

:::
::::

Now use the CLI to create the table that will back the Employee Portal Service
for this exercise.

```bash
./employee-portal create-table
```

#### What Happened?

Each exercise is set up to work with a new DynamoDB table to store
your item data.
This is to make is easier to compare and contrast as you move through the exercises.

### Step 2:

To start adding searchable encryption,
you are going to configure a Search Config.
[TODO break this down more]

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

TODO break down:
- configure the search config
- configure the beacon version
- configure the beacon over email
- configure the GSIs
- When do we actually add the GSIs to the table?

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 2 -->
```java
  // BEGIN EXERCISE 2 STEP 2
  public static ArrayList<StandardBeacon> MakeStandardBeacons() {
    ArrayList<StandardBeacon> beacons = new ArrayList<StandardBeacon>();
    beacons.add(StandardBeacon.builder()
        .name(EMPLOYEE_EMAIL_NAME)
        .length(8)
        .build());

    return beacons;
  }

  public static Constructor MakeGsi1TimecardPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(EMPLOYEE_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi1TimecardSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(START_TIME_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static CompoundBeacon MakeGsi1PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardPartitionKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI1_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi1SortKey() {
    ArrayList<SignedPart> signedParts = new ArrayList<SignedPart>();
    signedParts.add(SignedPart.builder().name(START_TIME_NAME).prefix(START_TIME_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardSortKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI1_SORT_KEY)
        .split(SPLIT)
        .signed(signedParts)
        .constructors(constructors)
        .build();
  }

  public static ArrayList<CompoundBeacon> MakeCompoundBeacons() {
    ArrayList<CompoundBeacon> beacons = new ArrayList<CompoundBeacon>();
    beacons.add(MakeGsi1PartitionKey());
    beacons.add(MakeGsi1SortKey());
    return beacons;
  }

  public static BeaconVersion MakeBeaconVersion(boolean ddbLocal) {
    return BeaconVersion.builder()
        .version(1)
        .keyStore(MakeKeyStore(ddbLocal))
        .keySource(MakeKeySource())
        .standardBeacons(MakeStandardBeacons())
        .compoundBeacons(MakeCompoundBeacons())
        .build();
  }

  public static SearchConfig MakeSearchConfig(boolean ddbLocal) {
    ArrayList<BeaconVersion> versions = new ArrayList<BeaconVersion>();
    versions.add(MakeBeaconVersion(ddbLocal));

    return SearchConfig.builder()
        .versions(versions)
        .writeVersion(1)
        .build();
  }
  // END EXERCISE 2 STEP 2
```

:::
::::

#### What Happened?

[TODO update below to fit above]

You have configured a few standard beacons
and composed them into a compound beacon.

Similarly to adding encryption two things will change.

When the applications uses the AWS Database Encryption SDK
to encrypt it will add beacons by:

1. Request your beacon key from the configured key store
(It will cache the beacon key for reuse)
1. Use the beacon key to derive HMAC keys for each beacon
1. HMAC, truncate, and store the values on the item
before writing it to DynamoDB

When you query on this beacon
using the AWS Database Encryption SDK
it will take take care of getting your results.
Under the hood it will:

1. Request you beacon key from the configured key store
(Or use the cached beacon key if it exists)
1. Use the beacon key to derive HMAC keys for each beacon in the query
1. Transform the query by HMACing the value and truncating the value
1. Send the transformed query to DynamoDB
1. Decrypt each result and compare it to the original plaintext query
1. Return all matches

### Step 3:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3 -->
```java
        // BEGIN EXERCISE 2 STEP 3
        .search(MakeSearchConfig(ddbLocal))
        // END EXERCISE 2 STEP 3
```

:::
::::

#### What Happened?

[TODO this is just a repeat of the above. maybe refactor the steps]
You have updated the DynamoDB Encryption Interceptor
with the Search Config you configured above.

Now your DynamoDB client will calculate and write beacons
when you put [TODO constructors logic].
When your DynamoDB client makes queries,
the interceptor will transform those queries
to appropriately use the beacons to retrieve your data.
[TODO some note on constraints here? which queries are not going to work?]

### Checking Your Work

If you want to check your progress,
or compare what you've done versus a finished example, 
check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/exercises/java/adding-searchable-encryption-configuration-complete
```

:::
::::

## Try it Out

Now that you have updated the code
to support this access pattern on encrypted data
you need to write the beacons to the database.

Before we get started, let's first reset the data in your table.

```bash
./load_data
```

Experiment using the API as much as you like. 

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
    // Example of getting an employee record
    // Example of the next access pattern that should fail 
    // Example of adding a new record that we can find
```

:::
::::

## Explore Further

Want to dive into more content related to this exercise?
Try out these links.

* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html" target="_blank">AWS KMS: Key Grants</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html" target="_blank">AWS KMS: Key Policies</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policy-modifying-external-accounts.html" target="_blank">AWS KMS: Cross-account KMS Key Usage</a>
* <a href="https://aws.amazon.com/dynamodb/global-tables/" target="_blank">Amazon DynamoDB: global tables</a>


# Next exercise

[TODO exercises are out of order?]
Ready for more?
Next you will work [add the remaining access patterns](./adding-the-remaining-access-patterns.md)
to our searchable encryption configuration.

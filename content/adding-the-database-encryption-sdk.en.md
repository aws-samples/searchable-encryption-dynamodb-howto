---
title : "Exercise 1"
weight : 100
---

# Exercise 1: Add the AWS Database Encryption SDK

In this section, you will add client-side encryption
to the example Employee Portal Service
using the [AWS Database Encryption SDK](TODO)
and [AWS Key Management Service](TODO).

## Background

In [Getting Started](./getting-started.md),
you set up a plaintext version of the
Employee Portal Service
and learned how to get and put items
into your DynamoDB table.

Now we are going to build a new version of this
Employee Portal Service that includes client-side encryption
with the [AWS Database Encryption SDK](#TODO).

We will use the AWS Database Encryption SDK
to encrypt items on the client,
before they are transmitted off of the host machine to DynamoDB.
When you encrypt items client-side, you choose which attributes
values to encrypt and which attributes to include in the signature.
When you retrieve these encrypted items from DynamoDB,
the client locally decrypts and verifies these items on your host.
In this way, DynamoDB never has access to the plaintext
of the item attributes you encrypt.
This process is called [client-side encryption](TODO).

To perform this encryption, the AWS Database Encryption SDK
will use a strategy known as [envelope encryption](TODO).
For every item that is encrypted, AWS KMS will provide
a unique `data key` that is responsible for encrypting that item.
You will configure the client to generate and protect these data keys
using the KMS Key that you set up in [Getting Started](./getting-started.md).

## Let's Go!

### Starting Directory

Make sure you are in the `exercises` directory for the language of your choice:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash
cd ~/environment/workshop/exercises/java/add-db-esdk-start
```

:::
::::

This workshop will break down each change you have to make into several steps.
For each step, the workshop will tell you which files to look at.
Within those files, look for comments that begins with: `BEGIN EXERCISE 1 STEP N`,
where `N` is the number step you are are.
This is where you will want to add new code.

### Step 1: Add the DB-ESDK Dependency

First, let's begin by updating our dependencies.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

This project uses [Gradle](https://gradle.org/) to build our application.
Take a look at our Gradle build file at `exercise-1/build.gradle.kts`.

:::
::::

Add the dependencies for:
- AWS Key Management Service
- AWS Database Encryption SDK
- AWS Cryptographic Materials Library
[TODO the MPL artifact id, and MPL/Gazelle versions still needs to be updated]

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
    api("javax.xml.bind:jaxb-api:2.3.1")
// BEGIN EXERCISE 1 STEP 1
    implementation(platform("software.amazon.awssdk:bom:2.19.1"))
    implementation("software.amazon.cryptography:aws-database-encryption-sdk-dynamodb:1.0-SNAPSHOT")
    implementation("software.amazon.cryptography:AwsCryptographicMaterialProviders:1.0-SNAPSHOT")
    implementation("software.amazon.awssdk:kms")
// END EXERCISE 1 STEP 1
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
```

:::
::::

#### What Happened?

The Gradle build is now configured with the dependencies needed for client-side encryption.
You added the AWS SDK client for KMS, which is what will be used to protect your encrypted items,
and the AWS Database Encryption SDK, which contains the code necessary to perform the encryption of your items.
You also added the AWS Cryptographic Materials Library, which contains the Keyring interface necessary
to configure the AWS Database Encryption SDK with AWS KMS.

You will see how we use each of these libraries in later steps.

### Step 2: Configure your KeyStore

To encrypt your items, you will be using a `Hierarchical Keyring` to wrap your data keys.
[TODO explain why and how it works]
Before you can configure your Hierarchical Keyring,
you need to create and populate the KeyStore that will back the Hierarchical Keyring.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

:::
::::

This file contains constants used by the Employee Portal Service.
Update this file to specify the following:
- The table name for this exercise.
  The application built in each exercise will use a different DynamoDB table in order to better demonstrate differences between each exercise.
- The table name for the KeyStore you will create.
- The KMS Key ARN that was created int [Getting Started](TODO).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public static class Constants {
    public static final boolean USE_LOCAL_DDB = true;

    // BEGIN EXERCISE 1 STEP 2
    public static final String TABLE_NAME = "Exercise1_Table";
    public static final String BRANCH_KEY_TABLE = "BranchKey_Table";
    public static final String BRANCH_KEY_KMS_ARN = "<your-kms-key-arn>";
    // END EXERCISE 1 STEP 2
```

:::
::::

The CLI supports a command that will create a KeyStore and
create a branch key in that KeyStore,
via a `CreateBranchKey` method.

Next, you will implement this method.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

First, update the file to import all the necessary classes for this exercise:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
import static sfw.example.dbesdkworkshop.Config.Constants.*;

// BEGIN EXERCISE 1 STEP 2
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.*;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.*;
import software.amazon.cryptography.dbencryptionsdk.structuredencryption.model.CryptoAction;
import software.amazon.cryptography.keystore.KeyStore;
import software.amazon.cryptography.keystore.model.CreateKeyOutput;
import software.amazon.cryptography.keystore.model.CreateKeyStoreInput;
import software.amazon.cryptography.keystore.model.KMSConfiguration;
import software.amazon.cryptography.keystore.model.KeyStoreConfig;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.MaterialProviders;
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsHierarchicalKeyringInput;
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig;
// END EXERCISE 1 STEP 2

public class AwsSupport {
```

:::
::::

Now, to implement `CreateBranchKey`:
1. Configure and instantiate a KeyStore.
1. Call the `CreateKeyStore` method on the KeyStore to create the DynamoDB table.
1. Call the `CreateKey` method on the KeyStore to create a new branch key in that KeyStore.
1. Return the Branch Key Id returned by the `CreateKey` call.

[TODO customers shouldn't have to reason about ddbLocal...]

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
  // BEGIN EXERCISE 1 STEP 2
  public static KeyStore MakeKeyStore(boolean ddbLocal)
  {
    return KeyStore.builder().KeyStoreConfig(
      KeyStoreConfig.builder()
        .ddbClient(GetClientBuilder(ddbLocal).build())
        .ddbTableName(BRANCH_KEY_TABLE)
        .logicalKeyStoreName(BRANCH_KEY_TABLE)
        .kmsClient(KmsClient.create())
        .kmsConfiguration(KMSConfiguration.builder()
          .kmsKeyArn(BRANCH_KEY_KMS_ARN)
          .build())
        .build()).build();
  }
  
  public static String CreateBranchKey(boolean ddbLocal) {
    final KeyStore keystore = MakeKeyStore(ddbLocal);    
    keystore.CreateKeyStore(CreateKeyStoreInput.builder().build());
    return keystore.CreateKey().branchKeyIdentifier();
  }
  // END EXERCISE 1 STEP 2
```

:::
::::

Now we can build our application and use the CLI to create our KeyStore and create a branch key.
Input the following into the terminal:

```bash
./employee-portal create-branch-key
```

This command outputs the branch key ID of the branch key just created.
Keep note of this ID, as we will need to add it to our configuration in the next step.

#### What Happened?

You have configured the KeyStore that will be used with your Hierarchical Keyring,
and populated the KeyStore with the branch key that will be used to protect your data.

The KeyStore class contains the helper method `CreateKeyStore` which will create
a DynamoDB table according to your KeyStore configuration.
The operation is idempotent.
If a DynamoDB table at the configured name already exists,
it will verify that the DynamoDB table is configured as expected.

The KeyStore class also contains a method responsible for creating
new branch keys in the KeyStore.
A globally unique id is created for each branch key.
In the next step you will configure your Hierarchical Keyring
to use this branch key by this branch key id.

### Step 3: Configure the Hierarchical Keyring

Now that we have a KeyStore with a branch key,
we can configure the Hierarchical Keyring.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

:::
::::

Update the Config file with the branch key id you received in [Step 2](#step-2-configure-your-keystore).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
    // END EXERCISE 1 STEP 3
    public static final String BRANCH_KEY_ID = "4e0315fc-ef45-4bd1-b8bc-49437c0a1e01";
    // END EXERCISE 1 STEP 3
```

:::
::::

Next, add a method which configures a Hierarchical Keyring,
using the same KeyStore configuration that is used by the CLI,
and using the branch key ID returned by the CLI in [Step 2](#step-2-configure-your-keystore).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
  // BEGIN EXERCISE 1 STEP 3
  public static IKeyring MakeHierarchicalKeyring(boolean ddbLocal)
  {
    final MaterialProviders matProv = MaterialProviders.builder()
      .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
      .build();

    final CreateAwsKmsHierarchicalKeyringInput keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
      .branchKeyId(BRANCH_KEY_ID)
      .keyStore(MakeKeyStore(ddbLocal))
      .ttlSeconds(6000l)
      .maxCacheSize(100)
      .build();
  
    return matProv.CreateAwsKmsHierarchicalKeyring(keyringInput);
  }
  // END EXERCISE 1 STEP 3
```

:::
::::

#### What Happened?

You have created a method that configures a Hierarchical Keyring.

This Hierarchical Keyring will use the KeyStore and branch key
you created in [Step 2](#step-2-configure-your-keystore)
to encrypt the data keys responsible for protecting your data.

In the next step, you will use this Keyring to configure client-side
encryption for your client.

### Step 4: Configure the DynamoDB Client with client-side encryption

Now that you have a Hierarchical Keyring,
the next step is to configure the DynamoDb Encryption Interceptor
and build the AWSK SDK client for DynamoDB with this interceptor.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

This file is responsible for creating the DynamoDB Client that the
Employee Portal Services uses to interact with DynamoDB.

Find `MakeDynamoDbClient` and update the configuration to add a new [Interceptor](TODO)
in the `override Configuration`.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public static DynamoDbClient MakeDynamoDbClient()
{
    return GetClientBuilder()
// BEGIN EXERCISE 1 STEP 4
    .overrideConfiguration(
        ClientOverrideConfiguration.builder()
        .addExecutionInterceptor(MakeInterceptor())
        .build())
// END EXERCISE 1 STEP 4
    .build();
}
```

:::
::::

Now create implement `MakeInterceptor` to create and configure
the AWS Database Encryption SDK's
DynamoDB Encryption Interceptor.
The DynamoDB Encryption Interceptor will encrypt DynamoDB items before they are sent to DynamoDB,
and will decrypt items after they are retrieved from DynamoDB.

For each table you want to client-side encrypt,
you need to configure a DynamoDB Table Encryption Config.
For this exercise, you only need to create one Table Encryption Config
for your one table.

For this table, configuration contains several parts:
1. Configure a [Crypto Action](TODO) for each attribute in your table.
1. Configure the Logical Table Name. This can be the same as the DynamoDB table name.
1. Specify the partition and sort key names on your table.
1. Configure the Hierarchical Keyring you implemented in [Step 3](#step-3-configure-the-hierarchical-keyring).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
  // BEGIN EXERCISE 1 STEP 4
  public static DynamoDbEncryptionInterceptor MakeInterceptor(boolean ddbLocal)
  {
    final IKeyring kmsKeyring = MakeHierarchicalKeyring(ddbLocal);

    HashMap<String, CryptoAction> actions = new HashMap<String, CryptoAction>();
    actions.put(PARTITION_KEY, CryptoAction.SIGN_ONLY);
    actions.put(SORT_KEY, CryptoAction.SIGN_ONLY);

    actions.put(ASSIGNEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ATTENDEES_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(AUTHOR_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(DESCRIPTION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(DURATION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(EMPLOYEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(EMPLOYEE_NAME_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(HOURS_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(LOCATION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(MANAGER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(MESSAGE_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ORGANIZER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ROLE_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(SEVERITY_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(STATUS_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(SUBJECT_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(TITLE_NAME, CryptoAction.ENCRYPT_AND_SIGN);

    // These attributes cannot be encrypted, as they are used in the primary key
    // or are needed in ranged searches
    actions.put(EMPLOYEE_NUMBER_NAME, CryptoAction.SIGN_ONLY);
    actions.put(MODIFIED_DATE_NAME, CryptoAction.SIGN_ONLY);
    actions.put(PROJECT_NAME_NAME, CryptoAction.SIGN_ONLY);
    actions.put(RESERVATION_NAME, CryptoAction.SIGN_ONLY);
    actions.put(START_TIME_NAME, CryptoAction.SIGN_ONLY);
    actions.put(TARGET_DATE_NAME, CryptoAction.SIGN_ONLY);
    actions.put(TICKET_NUMBER_NAME, CryptoAction.SIGN_ONLY);

    DynamoDbTableEncryptionConfig tableConfig = DynamoDbTableEncryptionConfig.builder()
      .logicalTableName(TABLE_NAME)
      .partitionKeyName(PARTITION_KEY)
      .sortKeyName(SORT_KEY)
      .attributeActionsOnEncrypt(actions)
      .keyring(kmsKeyring)
      .build();

    HashMap<String, DynamoDbTableEncryptionConfig> tables = new HashMap<String, DynamoDbTableEncryptionConfig>();
    tables.put(TABLE_NAME, tableConfig);
    DynamoDbTablesEncryptionConfig config = DynamoDbTablesEncryptionConfig.builder()
      .tableEncryptionConfigs(tables)
      .build();

    return DynamoDbEncryptionInterceptor.builder().config(config).build();
  }
  // END EXERCISE 1 STEP 4
```

:::
::::

#### What Happened?

You configured which attributes in your items are encrypted and signed.
Every attribute in your items are signed, meaning they are included in the signature calculation,
and verification of these values will fail if they are tampered with post-encryption.
Almost all attributes in your items are also encrypted.
The primary and sort key are not encrypted,
because you need to be able to easily get items based on their partition and sort values.
Other attributes may not be encrypted for similar reasons,
as they are either also used as a primary key value,
or otherwise are needed to be plaintext to support
ranged searched.
For all other values, you will encrypt them.
By the end of this workshop, you will see how
we can keep these items encrypted while still preserving the
rest of your desired access patterns.

Note that we have not configured any action for the attributes that the plaintext Employee Portal Service
used as it's GSIs. In the next step you will remove these GSIs from being written with your items,
as we will eventually need to make new GSIs that work with searchable encryption.
[TODO explain and link to GSI documentation]

Using the attribute actions you configured
and the Hierarchical Keyring you configured in [Step 3](#step-3-configure-the-hierarchical-keyring),
you have configured the DynamoDB Encryption Interceptor which will intercept DynamoDB
calls in order to perform client-side encryption.

With these changes, the DynamoDB Client built with this interceptor
will encrypt items as configured, locally, before they are put into DynamoDB,
and will decrypt items as configured, locally, after they are gotten from DynamoDB.

### Step 5: Stop writing to GSIs

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Project.java`.

:::
::::

As mentioned in the previous step, we will not longer be able to use the
GSIs that were used for the plaintext Employee Portal Service.

Go to our model file for `Project` and remove any code that populates
your old GSIs.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
    item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    item.put(SORT_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(STATUS_PREFIX + status));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Reservation.java`.

:::
::::

Update the code so that we no longer populate your
old GSIs when writing `Reservation` items.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
    item.put(PARTITION_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));
    item.put(SORT_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));

// BEGIN EXERCISE 1 STEP 5
    // String floor = location.get(FLOOR_NAME);
    // String room = location.get(ROOM_NAME);
    // String building = location.get(BUILDING_NAME);
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(ORGANIZER_EMAIL_PREFIX + organizerEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));

    // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(BUILDING_PREFIX + building));
    // item.put(GSI3_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Ticket.java`.

:::
::::

Update the code so that we no longer populate your
old GSIs when writing `Ticket` items.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(TICKET_NUMBER_PREFIX + ticketNumber));
    item.put(SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(AUTHOR_EMAIL_PREFIX + authorEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

    // item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(ASSIGNEE_EMIL_PREFIX + assigneeEmail));

    // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(SEVERITY_PREFIX + severity));
    // item.put(GSI3_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Timecard.java`.

:::
::::

Update the code so that we no longer populate your
old GSIs when writing `Timecard` items.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public Map<String, AttributeValue> toItem() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Meeting.java`.

:::
::::

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Meeting` items.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public Map<String, AttributeValue> toItem() {
    String floor = location.get(FLOOR_NAME);
    String room = location.get(ROOM_NAME);
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime ));

// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Employee.java`.

:::
::::

Update the code so that we no longer populate your
old GSIs when writing `Employee` items.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```java
public Map<String, AttributeValue> toItem() {
    String locTag = "";
    locTag = AppendStrWithPrefix(locTag, location.get(BUILDING_NAME), BUILDING_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(FLOOR_NAME), FLOOR_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(ROOM_NAME), ROOM_PREFIX);
    locTag = AppendStrWithPrefix(locTag, location.get(DESK_NAME), DESK_PREFIX);

    Map<String, AttributeValue> item = new HashMap<>();
    item.put(PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    item.put(SORT_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
// BEGIN EXERCISE 1 STEP 5
    // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
    // item.put(GSI1_SORT_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
    // item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(MANAGER_EMAIL_PREFIX + managerEmail));
    // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(CITY_PREFIX + location.get(CITY_NAME)));
    // item.put(GSI3_SORT_KEY, AttributeValue.fromS(locTag));
// BEGIN EXERCISE 1 STEP 5
```

:::
::::

#### What Happened?

You have updated the code so items will no longer populate your old GSI attributes.

Now you have everything set to start using the Employee Portal Service with client-side encryption.

### Checking Your Work

Want to check your progress, or compare what you've done versus a finished example?

Check out the code in one of the `-complete` folders to compare.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/exercises/java/add-esdk-complete
```

:::
::::

## Try it Out

Now that you have written the code,
let's try it out and see what it does.

First, let's create the table that will back the Employee Portal Service.
We have made this easy for you by providing a target within the CLI.

```bash
./employee-portal create-table
```

[Go to the DynamoDB AWS Console to confirm that your expected table is created](TODO).

Next, load up some test data into your portal!
We have provided a script that puts some sample data into your table.

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

The data that the CLI prints will appear as plaintext
because you have set up the CLI to locally decrypt items as soon as they are retrieved from DynamoDB.

Let's verify that the data is actually encrypted in DynamoDB.
(Go to your DynamoDB table in the AWS Console and explore your table items)[TODO link to DynamoDB table].
You should see that most of your item attributes.
now appear in DynamoDB as the `Bytes` DynamoDB type,
in an encrypted form.
You should also notice that there are two extra attributes written to our items.
`aws_dbe_head` contains our [material descrption](TODO),
which contains the metadata necessary for the AWS Database Encryption SDK
to understand how to decrypt the item.
`aws_dbe_foot` contains the signature calculated over our item.

Your data is encrypted in dynamodb, but you've built the Employee Portal Service
such that this encryption and decryption happens transparently
when you interact with the data via a client locally.

### Can we query?

In the previous step we just ran `get-employees` to get
all of our employees.
Under the hood, this [TODO].

We can verify that we are able to get a particular employee by primary key:

```bash
./employee-portal get-employees --employee-number=1234
```

However, what happens when we try to index on a different attribute?

```bash
./employee-portal get-employees --city=SEA
```

When we made this query to our plaintext Employee Portal Service,
we retrieved back all employees in Seattle,
but now we don't get any results back!

As you can see, because we are no longer writing to the GSIs
that our plaintext Employee Portal Service used to support more complex access patterns,
our CLI is currently unable to retrieve employees by anything
other than their primary key value.

### Put items into your encrypted table

Let's double check putting new items into our table
via the CLI still behaves as expected.

Put a new ticket into our table:

[TODO fill with approved sample data]

```bash
./employee-portal put-ticket --ticket-number=<ticketNumber> --modified-date=<modifiedDate> --author-email=<authorEmail> --assignee-email=<assigneeEmail> --severity=<severity> --subject=<subject> --message=<message>
```

Now verify that this ticket appears in our table:

```bash
./employee-portal get-tickets
```

You may additionally want to verify that this item is encrypted as expected
in DynamoDB. [TODO link to DynamoDB table]

### Verify KMS Usage

One final thing you may want to check is that your KMS Key
is being used as expected to protect your items.

Take a look at your [AWS CloudTrail logs](TODO) and
[TODO steps to find KMS log for item put above].

## Explore Further  --- BUG BUG

* **AWS Cloud Development Kit** - Check out the `~/environment/workshop/cdk` directory to see how the workshop resources are described using CDK.
* **Alice, Bob, and Friends** - <a href="https://en.wikipedia.org/wiki/Alice_and_Bob#Cast_of_characters" target="_blank">Who are Faythe and Walter?</a>
* [TODO]

# Next exercise

Now that you are encrypting and decrypting items in the Employee Portal Service,
let's move onto adding back in those GSIs which enable all of our interesting access patterns.
Move onto the next exercise:
[Adding a searchable encryption configuration](./adding-searchable-encryption-configuration.md)?

---
title : "Exercise 1"
weight : 100
---

<!-- !test program
./utils/check-block.sh ./workshop/java/exercise-1 <&0
 -->

# Exercise 1: Add the AWS Database Encryption SDK

In this section, you will add client-side encryption
to the example Employee Portal Service
using the [AWS Database Encryption SDK](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/)
and [AWS Key Management Service](https://docs.aws.amazon.com/kms/).

## Background

In [Getting Started](../getting-started.md),
you set the
Employee Portal Service
and learned how to get and put items
into your DynamoDB table.

Now we are going to build a new version of this
Employee Portal Service that includes client-side encryption
with the [AWS Database Encryption SDK](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/).

We will use the AWS Database Encryption SDK
to encrypt items on the client,
before they are transmitted off of the host machine to DynamoDB.
When you encrypt items client-side, you choose which attributes
values to encrypt and which attributes to include in the signature.
When you retrieve these encrypted items from DynamoDB,
the client locally decrypts and verifies these items on your host.
In this way, DynamoDB never has access to the plaintext
of the item attributes you encrypt.
This process is called [client-side encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/client-server-side.html).

To perform this encryption, the AWS Database Encryption SDK
will use a strategy known as [envelope encryption](https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#enveloping).
For every item that is encrypted,
a unique `data key` is generated that is responsible for encrypting that item.
You will configure the client to generate and protect these data keys
using the KMS Key that you set up in [Getting Started](../getting-started).

## Let's Go!

### Starting Directory

Make sure you are in the `exercises` directory for the language of your choice:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash
cd ~/environment/workshop/java/exercise-1
```

:::
::::

This workshop will break down each change you have to make into several steps.
For each step, the workshop will tell you which files to look at.
Within those files, look for comments that begins with: `BEGIN EXERCISE 1 STEP N`,
where `N` is the number step you are are.
This is where you will want to add new code.

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
    // BEGIN EXERCISE 1 STEP 1
    public static final String TABLE_NAME = "Exercise1_Table";
    // END EXERCISE 1 STEP 1
```

:::
::::

#### What Happened?

Each exercise is set up to work with a new DynamoDB table to store
your item data.
This is to make is easier to compare and contrast as you move through the exercises.

### Step 2: Add the DB-ESDK Dependency

Now, let's begin adding client-side encryption by updating our dependencies.

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

<!-- !test program
./utils/check-block.sh ./workshop/java/exercise-1 <&0
 -->

<!-- !test check java step 2 -->
```java
    // BEGIN EXERCISE 1 STEP 2
    implementation(platform("software.amazon.awssdk:bom:2.19.1"))
    implementation("software.amazon.cryptography:aws-database-encryption-sdk-dynamodb:1.0-SNAPSHOT")
    implementation("software.amazon.cryptography:AwsCryptographicMaterialProviders:1.0-SNAPSHOT")
    implementation("software.amazon.awssdk:kms")
    // END EXERCISE 1 STEP 2
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

### Step 3: Configure your Key Store

To encrypt your items, you will be using a [Hierarchical Keyring](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/use-hierarchical-keyring.html) to protect the
data keys that encrypt each item.

The Hierarchical Keyring establishes a key hierarchy,
where your KMS Key is the top of that key hierarchy.
The KMS Key is used to protect branch keys
that are stored in a Key Store backed by a different DynamoDB table.
These branch keys are used to protect the data keys
that are used to encrypt your items.
The Hierarchical Keyring locally caches these branch keys
so that you do not need to make a network call to DynamoDB
or KMS every time you need to encrypt or decrypt an item.

[TODO diagram?]

Before you can configure your Hierarchical Keyring,
you need to create and populate the Key Store that will back the Hierarchical Keyring.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `~/environment/workshop/config.toml`.

:::
::::

This file contains constants used by the Employee Portal Service.
Update this file to specify your KMS Key ARN.
If you are working through these exercises in an AWS classroom environment, a KMS Key has been created for you. Open the [Event dashboard](https://catalog.us-east-1.prod.workshops.aws/event/dashboard/en-US), grab the "KMSKeyArn" field value, and use it in the code snippet below:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}


<!-- Not tested, the public test vector keys are stored -->
```toml
# BEGIN EXERCISE 1 STEP 3a
branch_key_kms_arn = "<your-kms-key-arn>"
# END EXERCISE 1 STEP 3a
```

:::
::::

The CLI supports a command that will create a Key Store and
create a branch key in that Key Store,
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

<!-- !test check java step 3b -->
```java
// BEGIN EXERCISE 1 STEP 3b
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
// END EXERCISE 1 STEP 3b
```

:::
::::

Now, to implement `CreateBranchKey`:
1. Configure and instantiate a Key Store.
    - Put this in a new `CreateKeyStore` method so that we may reuse it later.
    - Use the KMS Key and Key Store table name that we defined earlier in this step.
    - For `logicalKeyStoreName`, use the Key Store's DynamoDB table name.
      This is the name that will be cryptographically bound to the branch keys for authentication.
    - The Key Store requires a DynamoDB client.
      This is the client that will be used to put and retrieve
      branch keys in the Key Store's backing DynamoDB table.
      [TODO how do we talk about the DDB Client being created here with ddbLocal]
    - The Key Store also requires a KMS Client.
      This is the client that will be used to call KMS GenerateWithoutPlaintext
      when creating the branch key, and call KMS Decrypt when your application
      will eventually need to decrypt this branch key for use in item encryption and decryption.
1. Call the `CreateKeyStore` method on the Key Store to create the DynamoDB table.
1. Call the `CreateKey` method on the Key Store to create a new branch key in that Key Store.
1. Return the Branch Key Id returned by the `CreateKey` call.

[TODO customers shouldn't have to reason about ddbLocal...]

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3c -->
```java
  // BEGIN EXERCISE 1 STEP 3c
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
  // END EXERCISE 1 STEP 3c
```

:::
::::

#### What Happened?

You have configured the Key Store that will be used with your Hierarchical Keyring,
and populated the Key Store with the branch key that will be used to protect your data.
This branch key is stored in an encrypted form in a new DynamoDB table,
protected by your KMS Key.

The Key Store class contains the helper method `CreateKeyStore` which will create
a DynamoDB table according to your Key Store configuration.
The operation is idempotent.
If a DynamoDB table at the configured name already exists,
it will verify that the DynamoDB table is configured as expected.

The Key Store class also the `CreateBranchKey` method which will create
new branch keys in the Key Store.
A globally unique id is created for each branch key.
In the next step you will configure your Hierarchical Keyring
to use this branch key by this branch key id.

### Step 4: Configure the Hierarchical Keyring

Now that we have a Key Store with a branch key,
we can configure the Hierarchical Keyring.

Add a method which configures a Hierarchical Keyring,
using the same Key Store configuration that is used by the CLI,
and using the branch key ID returned by the CLI in [Step 3](#step-3-configure-your-key-store).
Additionally, specify a TTL and cache size for the Hierarchical Keyring.
The TTL determines how long a branch key can be reused locally
before the Hierarchy Keyring is required to re-retrieve the material
from the Key Store and re-authenticate with AWS KMS.
The max cache size determine how many branch key entries
can be stored in the cache at one time before the
cache begins to evict older entries.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4b -->
```java
  // BEGIN EXERCISE 1 STEP 4b
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
  // END EXERCISE 1 STEP 4b
```

:::
::::

#### What Happened?

You have created a method that configures a Hierarchical Keyring.

This Hierarchical Keyring will use the Key Store and branch key
you created in [Step 3](#step-3-configure-your-key-store)
to encrypt the data keys responsible for protecting your data.
This Hierarchical Keyring will also cache these branch keys
according to your configuration, limiting how often
you call back to KMS.

In the next step, you will use this Keyring to configure client-side
encryption for your client.

### Step 5: Configure the DynamoDB Client with client-side encryption

Now that you have a Hierarchical Keyring,
the next step is to configure the DynamoDB Encryption Interceptor
and build the AWS SDK client for DynamoDB with this interceptor.
The DynamoDB Encryption Interceptor contains that logic that
performs client-side encryption with DynamoDB.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

This file is responsible for creating the DynamoDB Client that the
Employee Portal Services uses to interact with DynamoDB.

Find `MakeDynamoDbClient` and update the configuration to add a new [Interceptor](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/core/interceptor/ExecutionInterceptor.html)
in the `override Configuration`.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5a -->
```java
        // BEGIN EXERCISE 1 STEP 5a
        .overrideConfiguration(
          ClientOverrideConfiguration.builder()
            .addExecutionInterceptor(MakeInterceptor(shared.ddbLocal))
            .build())
        // END EXERCISE 1 STEP 5a
```

:::
::::

Now implement a `MakeInterceptor` method to create and configure
the DynamoDB Encryption Interceptor.

For each table you want to client-side encrypt,
you need to configure a DynamoDB Table Encryption Config.
For this exercise, you only need to create one Table Encryption Config
for your one table.

For this table, configuration contains several parts:
1. Configure a [Crypto Action](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/concepts.html#crypt-actions) for each attribute in your table.
  - For every attribute, you can `ENCRYPT_AND_SIGN`, `SIGN_ONLY`, or `DO_NOTHING`.
    For this exercise, you will `ENCRYPT_AND_SIGN` as many attributes as possible,
    and `SIGN_ONLY` the rest.
1. Configure the Logical Table Name. This is the name that is cryptographically bound
   to your items for the purposes of authentication.
   For this, you can use the same value as the DynamoDB table name.
1. Specify the partition and sort key names on your table.
1. Configure the Hierarchical Keyring you implemented in [Step 4](#step-4-configure-the-hierarchical-keyring).

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5b -->
```java
  // BEGIN EXERCISE 1 STEP 5b
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
  // END EXERCISE 1 STEP 5b
```

:::
::::

#### What Happened?

You configured which attributes in your items are encrypted and signed.
In the configuration you created,
every attribute in your items is signed,
meaning all attributes are included in the signature calculation.
This means that verification of your item will fail if any are tampered with post-encryption.
Almost all attribute values in your items are also encrypted.
The primary and sort key are not encrypted
because you need to be able to easily get items based on their partition and sort values.
Other attributes may not be encrypted for similar reasons,
as they are either also used as a primary key value,
or otherwise are needed to be plaintext to support
ranged searched.
By the end of this workshop, you will see how
we can keep these items encrypted while still preserving the
rest of your desired access patterns.

Using the attribute actions you configured
and the Hierarchical Keyring you configured in [Step 3](#step-3-configure-the-hierarchical-keyring),
you have configured the DynamoDB Encryption Interceptor which will intercept DynamoDB
calls in order to perform client-side encryption.

With these changes, the DynamoDB Client built with this interceptor
will encrypt items as configured, before they are put into DynamoDB,
and will decrypt items as configured, after they are retrieved from DynamoDB.

### Step 6: Tying it together

Now we can build our application and use the CLI to create our Key Store and create a branch key.
Input the following into the terminal:

<!-- !test program
set -o pipefail
if [[ -n "$USE_DDB_LOCAL" ]]; then
  BRANCH_KEY_ID=$(./workshop/java/exercise-1/employee-portal create-branch-key -l | tail -n 1 | sed 's/.*: //' | sed 's/^/\\\"/; s/$/\\\"/')
else
  BRANCH_KEY_ID=$(./workshop/java/exercise-1/employee-portal create-branch-key | tail -n 1 | sed 's/.*: //' | sed 's/^/\\\"/; s/$/\\\"/')
fi

exit_code=$?
if [ $exit_code -ne 0 ]; then
  exit $exit_code
fi

./utils/sed-add-change.sh "branch_key_id.*" "branch_key_id = $BRANCH_KEY_ID" ./workshop/config.toml
-->

<!-- !test check create-branch-key -->
```bash
./employee-portal create-branch-key
```

This command outputs the branch key ID of the branch key just created.
Keep note of this ID, as we will need to add it to our configuration.

Go to `~/environment/workshop/config.toml`.

Update the Config file with the branch key id you received just created.

<!-- not tested, this is accomplished in the test create-branch-key above -->
```toml
# BEGIN EXERCISE 1 STEP 6
branch_key_id = "<your-branch-key-id>"
# END EXERCISE 1 STEP 6
```

Now use the CLI to create the table that will back the Employee Portal Service
for this exercise.

<!-- !test program
cd ./workshop/java/exercise-1

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

### Checking Your Work

Want to check your progress, or compare what you've done versus a finished example?

Check out the code in one of the `-complete` folders to compare.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/workshop/java/add-esdk-complete
```

:::
::::

## Try it Out

Now that you have written the code,
let's try it out and see what it does.

### Examine your Key Store

First, let's take a look at the Key Store that we created.
Go to the [AWS Console for your Key Store table](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=BranchKey_Table).

Here, you should see that there exists two entries.
One contains a version under the `type` sort attribute.
This is the branch key that is responsible for
protecting your data keys.
The key material for this branch key is itself client-side encrypted
by your KMS Key, under the `enc` attribute.
The other item that shares the branch key id is called a beacon key.
Ignore the beacon key for now, we will use it in a later exercise.

The Hierarchical Keyring will get this branch key and decrypt it
with your KMS key the first time you encrypt or decrypt an item.
It will then cache this material so that the next time you encrypt
or decrypt you do not need to make another call to DynamoDB or KMS.
The Hierarchical Keyring will use this locally cached branch key
for as long as the TTL you configured, at which point it will remove
the branch key from the cache and need to retrieve and re-decrypt
the branch key from DynamoDB.

### Examine your data table

Now let's look at the DynamoDB table that store the data
from the Employee Portal Service.
Go to the [DynamoDB AWS Console](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#tables) to confirm that your expected table is created.

Next, load up some test data into your portal!

<!-- !test check load-data -->
```bash
./load-data
```

You should see that most of your item attributes.
now appear in DynamoDB as the `Bytes` DynamoDB type,
in an encrypted form.
DynamoDB never sees the plaintext form for these attribute values.

You should also notice that there are two extra attributes written to our items.
`aws_dbe_head` contains our [material description](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/concepts.html#material-description),
which contains the metadata necessary for the AWS Database Encryption SDK
to understand how to decrypt the item.
`aws_dbe_foot` contains the signature calculated over our item.

### Retrieve items from your encrypted table

Similar to how we got and put records into the plaintext Employee Portal Service,
you can use the CLI to retrieve and put records into our Employee Portal Service
with client-side encryption.

To start, let's retrieve all of our employees again:

<!-- !test in get-employees -->
```bash
./employee-portal get-employees
```

Expected output:

<!-- !test out get-employees -->
```
employeeNumber employeeEmail       managerEmail        name                title     location
4567           david@gmail.com     zorro@gmail.com     David Jones         SDE6      {city=NYC, desk=3, floor=1, building=22, room=2}
3456           charlie@gmail.com   zorro@gmail.com     Charlie Jones       SDE7      {city=SEA, desk=5, floor=4, building=44, room=2}
1234           able@gmail.com      zorro@gmail.com     Able Jones          SDE9      {city=SEA, desk=3, floor=12, building=44, room=2}
2345           barney@gmail.com    zorro@gmail.com     Barney Jones        SDE8      {city=SEA, desk=4, floor=12, building=44, room=2}
```

The data that the CLI prints will appear as plaintext
because you have set up the CLI to locally decrypt items as soon as they are retrieved from DynamoDB.
Your data is encrypted in dynamodb, but you've built the Employee Portal Service
such that this encryption and decryption happens transparently
when you interact with the data with the CLI.

### Can we query?

In the previous step we just ran `get-employees` to get
all of our employees.

We can verify that we are able to get a particular employee by primary key:

<!-- !test in get-employees-1234 -->
```bash
./employee-portal get-employees --employee-number=1234
```

Expected output:

<!-- !test out get-employees-1234 -->
```
employeeNumber employeeEmail       managerEmail        name                title     location
1234           able@gmail.com      zorro@gmail.com     Able Jones          SDE9      {city=SEA, desk=3, floor=12, building=44, room=2}
```

However, what happens when we try to index on a different attribute?

<!-- !test in get-employees-SEA -->
```bash
./employee-portal get-employees --city=SEA
```

When we made this query to our plaintext Employee Portal Service,
we retrieved back all employees in Seattle,
but now we don't get any results back!
<!-- !test out get-employees-SEA -->
```
employeeNumber employeeEmail       managerEmail        name                title     location
```

As you can see, because we are no longer writing to the Global Secondary Indexes
that our plaintext Employee Portal Service used,
our CLI is currently unable to retrieve employees by anything
other than their primary key value.

### Put items into your encrypted table

Let's double check that putting new items into our table
via the CLI still behaves as expected.

Put a new ticket into our table:

<!-- !test check put-ticket -->
```bash
./employee-portal put-ticket --ticket-number=3 --modified-date=2022-10-07T15:32:25 --author-email=barney@gmail.com --assignee-email=charlie@gmail.com --severity=3 --subject="Bad Bug Followup" --message="We should follow up on the Bad Bug"
```

Now verify that this ticket appears in our table:

<!-- !test in get-tickets -->
```bash
./employee-portal get-tickets
```

Expected output:

<!-- !test out get-tickets -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
2                   2022-10-06T14:32:25      zorro@gmail.com     charlie@gmail.com   3           Easy Bug            This seems simple enough
2                   2022-10-08T14:32:25      charlie@gmail.com   able@gmail.com      3           Easy Bug            that's in able's code
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
1                   2022-10-07T15:32:25      able@gmail.com      charlie@gmail.com   3           Bad Bug             Charlie should handle this
3                   2022-10-07T15:32:25      barney@gmail.com    charlie@gmail.com   3           Bad Bug Followup    We should follow up on the Bad Bug
```

You may additionally want to verify that this item is encrypted as expected
in [your DynamoDB table](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise1_Table).

### Verify KMS Usage

One final thing you may want to check is that your KMS Key
is being used as expected to protect your items.

Take a look at your [AWS CloudTrail logs](TODO) and
[TODO steps to find KMS log for item put above].

[TODO some note about the Hierarchy Keyring is not 1:1 with KMS calls]

## Explore Further  --- BUG BUG

* **AWS Cloud Development Kit** - Check out the `~/environment/workshop/cdk` directory to see how the workshop resources are described using CDK.
* **Alice, Bob, and Friends** - <a href="https://en.wikipedia.org/wiki/Alice_and_Bob#Cast_of_characters" target="_blank">Who are Faythe and Walter?</a>
* [TODO]

# Next exercise

Now that you are encrypting and decrypting items in the Employee Portal Service,
let's move onto adding back in those Global Secondary Indexes which enable all of our interesting access patterns.
Move onto the next exercise:
[Adding a searchable encryption configuration](../exercise-2)

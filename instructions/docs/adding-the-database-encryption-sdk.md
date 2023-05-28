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

=== "Java"

    ```bash
    cd ~/environment/workshop/exercises/java/add-db-esdk-start
    ```

This workshop will break down each change you have to make into several steps.
For each step, the workshop will tell you which file to look at.
Within that file, look for a comment that begins with: `BEGIN EXERCISE 1 STEP N`,
where `N` is the number step you are are.
This is where you will want to add new code.

### Step 1: Add the DB-ESDK Dependency

First, let's begin by updating our dependencies.

=== "Java"

    This project uses [Gradle](https://gradle.org/) to build our application.
    Take a look at our Gradle build file at `exercise-1/build.gradle.kts`.

Add the dependencies for:
- AWS Key Management Service
- AWS Database Encryption SDK
- AWS Cryptographic Materials Library
[TODO the MPL artifact id still needs to be updated]

=== "Java"

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

#### What Happened?

The Gradle build is now configured with the dependencies needed for client-side encryption.
You configured the AWS SDK client for KMS, which is what will be used to protect your encrypted items,
and the AWS Database Encryption SDK, which contains the code necessary to perform the encryption of your items.
You also configured the AWS Cryptographic Materials Library, which contains the Keyring interface necessary
to configure the AWS Database Encryption SDK with AWS KMS.

You will see how we use each of these libraries in later steps.

### Step 2: Configure your Table Name and Key Ids.

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

This file contains constants used by our application, which are specific to this exercise.
Update this file to specify the following:
- The table name for Exercise 1.
- The table name for the KeyStore we will create. [TODO should we be created this at all yet?]
- The KMS Key ARN that was created int [Getting Started](TODO)
- The branch key ID that [TODO at this point the key has not been created yet. Where are the steps to create the Keystore?]

=== "Java"

    ```java
    public static class Constants {
        public static final boolean USE_LOCAL_DDB = true;

        // BEGIN EXERCISE 1 STEP 2
        public static final String TABLE_NAME = "Exercise1_Table";
        public static final String BRANCH_KEY_TABLE = "BranchKey_Table";
        public static final String BRANCH_KEY_KMS_ARN = "<kms-key-id>";
        public static final String BRANCH_KEY_ID = "<branch-key-id>";
        // END EXERCISE 1 STEP 2
    ```

#### What Happened?

You have configured what tables and keys will be used for this exercise.

The `TABLE_NAME` is the name of the DynamoDB table specific to this exercise.
The next exercise will configure a different table so that it is easier
to see changes as we move through the exercises.

The `BRANCH_KEY_TABLE` is the name for the KeyStore,
which is a new DynamoDB table that will be created in future step.

The `BRANCH_KEY_KMS_ARN` identifies the KMS Key that is responsible for protecting
the key material in the KeyStore.
This value is set to the KMS Key that was created for you in [Getting Started](TODO).

[TODO branch key. Where does this fit in.]

### Step 3: Import what's needed for client-side encryption configuration

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

This file is currently responsible for creating the DynamoDB Client that the
Employee Portal Services uses to interact with DynamoDB.

This client needs to be updated to use client-side encryption.

First, let's import everything that is necessary for this configuration.

=== "Java"

    ```java
    // BEGIN EXERCISE 1 STEP 3
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
    // END EXERCISE 1 STEP 3

    /** Helper to pull required Document Bucket configuration keys out of the configuration system. */
    public class AwsSupport {
    ```

#### What Happened?

You have just imported all of the classes that we will need to configure client-side encryption.

### Step 4: Update the configuration for the DynamoDB Client

In this same file, note that there exists a method responsible for creating the
DynamoDB Client.

In that method, update the configuration to add a new [Interceptor](TODO)
in the `override Configuration`.

=== "Java"

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

#### What Happened?

[TODO this will be specific to Java, so a bit unsure how to talk about this. It seems likely that once we add Python we will want to refactor how we break this up into various steps. Do we want to simplify right now so that we are only ever talking about Java?]

You have updated the DynamoDB Client so that it builds with a new Interceptor
(which we will implement in the next step).
This Interceptor will be responsible for encrypting items in DynamoDB API requests
before they are sent to DynamoDB, and decrypting these encrypted items in DynamoDB API responses.

### Step 5: Configure the DynamoDB Encryption Interceptor

In this same file, create a new `MakeInterceptor` method that is
responsible for creating and configuring the AWS Database Encryption SDK's
DynamoDB Encryption Interceptor.

[TODO can we ignore the KeyStore configuration now?]

[TODO each of these should really be broken down into it's own step]

[TODO as well as steps for creating the branch key we need via the KeyStore]

This configuration contains several parts:
1. Create the Hierarchical Keyring [TODO Are we just using the Hierarchical Keyring everywhere?]
1. Configure the cryptographic actions for each attribute that may be included in the items in our table.
1. Create and configure the DynamoDB Encryption Interceptor to encrypt your table with the above
   crypto actions and keyring.

=== "Java"

    ```java
    // BEGIN EXERCISE 1 STEP 5
    public static KeyStore MakeKeyStore()
    {
        return KeyStore.builder().KeyStoreConfig(
        KeyStoreConfig.builder()
            .ddbClient(MakeDynamoDbClientPlain())
            .ddbTableName(BRANCH_KEY_TABLE)
            .logicalKeyStoreName(BRANCH_KEY_TABLE)
            .kmsClient(KmsClient.create())
            .kmsConfiguration(KMSConfiguration.builder()
            .kmsKeyArn(BRANCH_KEY_KMS_ARN)
            .build())
            .build()).build();
    }

    public static String CreateBranchKey() {
        final KeyStore keystore = MakeKeyStore();    
        keystore.CreateKeyStore(CreateKeyStoreInput.builder().build());
        return keystore.CreateKey().branchKeyIdentifier();
    }

    public static DynamoDbEncryptionInterceptor MakeInterceptor()
    {
        final MaterialProviders matProv = MaterialProviders.builder()
        .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
        .build();

        final CreateAwsKmsHierarchicalKeyringInput keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
        .branchKeyId(BRANCH_KEY_ID)
        .keyStore(MakeKeyStore())
        .ttlSeconds(6000l)
        .maxCacheSize(100)
        .build();
    
        final IKeyring kmsKeyring = matProv.CreateAwsKmsHierarchicalKeyring(keyringInput);

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
    // END EXERCISE 1 STEP 5
    ```

#### What Happened?

You have configured the DynamoDB Encryption Interceptor which will intercept DynamoDB
calls in order to perform client-side encryption.

You configured a Hierarchical Keyring that is responsible for encrypting the data keys
that protect your items.
[TODO if we use the Hierarchical Keyring we really need to go into more detail on the KeyStore and such here]

You configured which attributes in your items are encrypted and signed.
Every attribute in your items are signed, meaning they are included in the signature calculation,
and verification of these values will fail if they are tampered with post-encryption.
Almost all attributes in your items are also encrypted.
The primary and sort key are not encrypted,
because you need to be able to easily get items based on their partition and sort values.
Other attributes may not be encrypted for similar reasons,
as they are either also used as a primary key value,
or otherwise are needed to be un-encrypted to support
ranged searched.
For all other values, we will encrypt them.
By the end of this workshop, you will see how
we can keep these items encrypted while still preserving the
rest of our desired access patterns.

Note that we have not configured any action for the attributes that the plaintext Employee Portal Service
used as it's GSIs. Future steps will remove these GSIs from our table,
as we will eventually need to make new ones that work with searchable encryption.
[TODO explain and link to GSI documentation]

Finally, you configured the DynamoDB Encryption Interceptor using the above configurations,
along with configuration specific to the table you will be encrypting items for.

With these changes, the DynamoDB Client that we are now configuring with this interceptor
will encrypt items as configured, locally, before they are put into DynamoDB,
and will decrypt items as configured, locally, after they are gotten from DynamoDB.

### Step 6: Remove Projects use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Project.java`.

[TODO Logically, it makes sense to put all of the next steps as a single step.]

As mentioned in the previous step, we will not longer be able to use the
GSIs that were used for the plaintext Employee Portal Service.

Go to our model file for `Project` and remove any code that populates
your old GSIs.

=== "Java"

    ```java
        item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
        item.put(SORT_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
    // BEGIN EXERCISE 1 STEP 6
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(STATUS_PREFIX + status));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
    // BEGIN EXERCISE 1 STEP 6
    ```

#### What Happened?

You have updated the code so that `Project` items will no longer populate your old GSI attributes.

### Step 7: Remove Reservations use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Reservation.java`.

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Reservation` items.

=== "Java"

    ```java
        item.put(PARTITION_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));
        item.put(SORT_KEY, AttributeValue.fromS(RESERVATION_PREFIX + reservation));

    // BEGIN EXERCISE 1 STEP 7
        // String floor = location.get(FLOOR_NAME);
        // String room = location.get(ROOM_NAME);
        // String building = location.get(BUILDING_NAME);
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(ORGANIZER_EMAIL_PREFIX + organizerEmail));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));

        // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(BUILDING_PREFIX + building));
        // item.put(GSI3_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));
    // BEGIN EXERCISE 1 STEP 7 
    ```

#### What Happened?

You have updated the code so that `Reservation` items will no longer populate your old GSI attributes.

### Step 8: Remove Ticket use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Ticket.java`.

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Ticket` items.

=== "Java"

    ```bash
    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(PARTITION_KEY, AttributeValue.fromS(TICKET_NUMBER_PREFIX + ticketNumber));
        item.put(SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

    // BEGIN EXERCISE 1 STEP 8
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(AUTHOR_EMAIL_PREFIX + authorEmail));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));

        // item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(ASSIGNEE_EMIL_PREFIX + assigneeEmail));

        // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(SEVERITY_PREFIX + severity));
        // item.put(GSI3_SORT_KEY, AttributeValue.fromS(MODIFIED_DATE_PREFIX + modifiedDate));
    // BEGIN EXERCISE 1 STEP 8
    ```

#### What Happened?

You have updated the code so that `Ticket` items will no longer populate your old GSI attributes.

### Step 9: Remove Timecard's use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Timecard.java`.

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Timecard` items.

=== "Java"

    ```java
    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(PARTITION_KEY, AttributeValue.fromS(PROJECT_NAME_PREFIX + projectName));
        item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
    // BEGIN EXERCISE 1 STEP 9
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime));
    // BEGIN EXERCISE 1 STEP 9
    ```

#### What Happened?

You have updated the code so that `Timecard` items will no longer populate your old GSI attributes.

### Step 10: Remove Meeting's use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Meeting.java`.

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Meeting` items.

=== "Java"

    ```java
    public Map<String, AttributeValue> toItem() {
        String floor = location.get(FLOOR_NAME);
        String room = location.get(ROOM_NAME);
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
        item.put(SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime ));

    // BEGIN EXERCISE 1 STEP 10
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(START_TIME_PREFIX + startTime + "." + FLOOR_PREFIX + floor + "." + ROOM_PREFIX + room));
    // BEGIN EXERCISE 1 STEP 10
    ```

#### What Happened?

You have updated the code so that `Meeting` items will no longer populate your old GSI attributes.

### Step 1: Update Employee's use of GSIs

=== "Java"

    Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/datamodel/Employee.java`.

Similar to our previous step, update the code so that we no longer populate your
old GSIs when writing `Employee` items.

=== "Java"

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
    // BEGIN EXERCISE 1 STEP 11
        // item.put(GSI1_PARTITION_KEY, AttributeValue.fromS(EMPLOYEE_EMAIL_PREFIX + employeeEmail));
        // item.put(GSI1_SORT_KEY, AttributeValue.fromS(EMPLOYEE_NUMBER_PREFIX + employeeNumber));
        // item.put(GSI2_PARTITION_KEY, AttributeValue.fromS(MANAGER_EMAIL_PREFIX + managerEmail));
        // item.put(GSI3_PARTITION_KEY, AttributeValue.fromS(CITY_PREFIX + location.get(CITY_NAME)));
        // item.put(GSI3_SORT_KEY, AttributeValue.fromS(locTag));
    // BEGIN EXERCISE 1 STEP 11
    ```

#### What Happened?

You have updated the code so that `Employee` items will no longer populate your old GSI attributes.

Now you have everything set to start using the Employee Portal Service with client-side encryption.

### Checking Your Work

Want to check your progress, or compare what you've done versus a finished example?

Check out the code in one of the `-complete` folders to compare.

=== "Java"

    ```bash 
    cd ~/environment/workshop/exercises/java/add-esdk-complete
    ```

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

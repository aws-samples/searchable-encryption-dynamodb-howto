---
title : "Exercise 2"
weight : 200
---

<!-- !test program
./utils/check-block.sh ./workshop/java/exercise-2 <&0
 -->

# Exercise 2: Adding Searchable Encryption Configuration

In this section, you will configure the AWS Database Encryption SDK to use searchable encryption.

## Background

Your Employee Portal will encrypt items locally before you put them
into your table
and will decrypt locally when you get items back from your table.

This presents us with an interesting problem.
If your table only ever sees your data in encrypted form,
how can you ever effectively query on that encrypted data?

To accomplish this, the AWS Database Encryption SDK for DynamoDB
includes the [searchable encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html) feature,
which allows you to calculate and store
[beacons](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/beacons.html) alongside your data.
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
Before you use [searchable encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html)
for your own use case, please read our [AWS documentation](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/what-is-database-encryption-sdk.html)
to ensure that [beacons are right for your use case](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html#are-beacons-right-for-me).
You should also discuss this feature with your security teams
to ensure it meets security requirements in your organization.

To use [searchable encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html),
you need another kind of key: a beacon key.
This beacon key will be used during both write and query
in order to calculate the truncated HMACs ([hash-based message authentication codes](https://en.wikipedia.org/wiki/HMAC))
that comprise beacons.

In [Exercise 1](../exercise-1), you created a Key Store with a branch key.
The `CreateBranchKey` API additionally created a second key for
you that is associated with this branch key.
This second key, which we will call the beacon key,
will be the key responsible for generating beacons
alongside your encrypted data. 

By configuring searchable encryption with this beacon key,
you will be able to enable queries over your encrypted attributes.

In this exercise you will add support for querying
for timecards based on email and start time.

## Let's Go!

### Starting Directory

If you just finished [Adding the Database Encryption SDK](../exercise-1), you are all set.

If you aren't sure, or want to catch up,
jump into the `exercise-1` directory for the language you are using:

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/java/exercise-2
```

:::
::::

### Step 1: Create your table

First, create a new DynamoDB table that will contain the data for this exercise.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Look at `exercise-1/src/main/java/sfw/example/dbesdkworkshop/Config.java`.

:::
::::

Update the table name to something specific to this exercise.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1a -->
```java
    // BEGIN EXERCISE 2 STEP 1a
    public static final String TABLE_NAME = "Exercise2_Table";
    // END EXERCISE 2 STEP 1a
```

:::
::::

Next, in order to support searchable encryption
for this new access pattern,
the table you create will need to be updated to
include a new Global Secondary Index that uses a new
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

<!-- !test check java step 1b -->
```java
      // BEGIN EXERCISE 2 STEP 1b
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
      // END EXERCISE 2 STEP 1b
```

:::
::::

In the same file, update the code
so that this new attribute is included
in the attribute definitions for table creation.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 1c -->
```java
    // BEGIN EXERCISE 2 STEP 1c
      .attributeName(BEACON_PREFIX + GSI1_PARTITION_KEY)
    // END EXERCISE 2 STEP 1c
```

:::
::::

#### What Happened?

Each exercise is set up to work with a new DynamoDB table to store
your item data.
This is to make is easier to compare and contrast as you move through the exercises.

### Step 2: Configure Standard Beacons

To start adding searchable encryption,
first configure the standard beacons over
the encrypted attributes that you will want to query.

To support querying timecards by email,
you will need to configure a beacon on the email attribute.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

Go to `exercise-1/src/main/java/sfw/example/dbesdkworkshop/AwsSupport.java`.

:::
::::

Create a method that will create the configurations for
all the standard beacons used in your table.
Right now, this will just be a standard beacon over the email attribute.

To create a beacon that is calculated over the email attribute,
configure `name` as "authorEmail".

The beacon length you should choose depends on your dataset,
and is a tradeoff between security and performance.
See [Choosing a Beacon Length](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-length.html) in our docs for more
information.

For this example, choose a length of 8.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 2 -->
```java
  // BEGIN EXERCISE 2 STEP 2
  public static ArrayList<StandardBeacon> MakeStandardBeacons() {
    ArrayList<StandardBeacon> beacons = new ArrayList<StandardBeacon>();
    beacons.add(StandardBeacon.builder()
        .name(AUTHOR_EMAIL_NAME)
        .length(8)
        .build());

    return beacons;
  }
  // END EXERCISE 2 STEP 2
```

:::
::::

#### What Happened?

You have created a standard beacon configuration over the email attribute.

When this is later configured with your client, the client
will calculate a beacon value over the plaintext email attribute,
and write that value to a new attribute named "aws_dbe_b_authorEmail".
During queries, the client will replace anywhere you specify an author email
with the beacon value instead.

### Step 3: Configure Compound Beacon for Global Secondary Index

With the standard beacon created,
you can now create the [compound beacon](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/choosing-beacon-type.html#plan-compound-beacons) that you will use as the value
for your new Global Secondary Index.

The value written to this Global Secondary Index in this exercise
will be exactly the standard beacon configured in the previous step.
When you add support for more access patterns in later exercises,
the value written to this attribute will depend on the item being written.

To start, you need to tell this compound beacon how
it should be constructed.
Configure a beacon constructor that contains a single, required part: author email.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3a -->
```java
  // BEGIN EXERCISE 2 STEP 3a
  public static Constructor MakeGsi1TimecardPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(AUTHOR_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  // END EXERCISE 2 STEP 3a
```

:::
::::

Now configure the compound beacon,
giving it the same name as your original Global Secondary Index, "PK1".

For `encryptedParts`, specify name as "authorEmail" and the unique prefix "CE-".
Also configure the constructor you configured above.

For `split`, define a character that does not appear in any of the data you are not client-side encrypting.
For this example, `SPLIT` uses "^".

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 3b -->
```java
  // BEGIN EXERCISE 2 STEP 3b
  public static CompoundBeacon MakeGsi1PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(AUTHOR_EMAIL_NAME).prefix(AUTHOR_EMAIL_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardPartitionKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI1_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }
  // END EXERCISE 2 STEP 3b
```

:::
::::

#### What Happened?

You have added a configuration for a new compound beacon which will be used
as the partition key in a Global Secondary Index on your table.

Because this compound beacon contains a constructor which specifies a required email field,
any time an item with a "email" field is written, the client will write to this compound beacon.

The value used in the beacon will include only the email standard beacon.
For example, if the calculated beacon value for "zorro@gmail.com" is "a",
then the value written to this field will be "CE-a".

### Step 4:

Now, you will similarly create a new compound beacon to be used as
the sort key in your new Global Secondary Index.

For the access pattern we are supporting in this exercise,
this sort key will just contain the plaintext modified date
of your tickets.

The modified date attribute needs to not be encrypted in order to support
inequalities on this sort key.
For example, a query that gets the tickets within a specific time range.

To start, configure the constructor for this compound beacon.
You want this beacon to be written to if the item contains the modified date attribute.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4a -->
```java
  // BEGIN EXERCISE 2 STEP 4a
  public static Constructor MakeGsi1TimecardSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(MODIFIED_DATE_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }
  // END EXERCISE 2 STEP 4a
```

:::
::::

Now configure the compound beacon which will be the sort key
in your new Global Secondary Index.

Configure the name as the sort key of your Global Secondary Index.

Instead of configuring the modified date as an encrypted part,
add it to the configuration as a signed part.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 4b -->
```java
  // BEGIN EXERCISE 2 STEP 4b
  public static CompoundBeacon MakeGsi1SortKey() {
    ArrayList<SignedPart> signedParts = new ArrayList<SignedPart>();
    signedParts.add(SignedPart.builder().name(MODIFIED_DATE_NAME).prefix(MODIFIED_DATE_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardSortKeyConstructor());

    return CompoundBeacon.builder()
        .name(GSI1_SORT_KEY)
        .split(SPLIT)
        .signed(signedParts)
        .constructors(constructors)
        .build();
  }
  // END EXERCISE 2 STEP 4b
```

:::
::::

#### What Happened?

You have added a configuration for a new compound beacon which will be used
as the sort key in a Global Secondary Index on your table.
Because this compound beacon only contains signed parts (not encrypted parts)
the value is written to the attribute "SK1" instead of "aws_dbe_b_SK1".

Because this compound beacon contains a constructor which specifies a required modified date attribute,
any time an item with a modified date attribute is written, the client will write to this compound beacon.

The value used in the beacon will include only the plaintext value of the modified date.
For example, if the calculated beacon value for "modifiedDate" is "2023-06-13",
then the value written to this field will be "M-2023-06-13".

### Step 5: Add the Beacons to the Interceptor

Now that you have configured your beacons, the only
thing left to do is add these beacons to the configuration
of the DynamoDB Encryption Interceptor.

First configure a `BeaconVersion` which includes:
- The standard beacon you configured above
- The compound beacons your configured above
- The Key Store you configured in [Exercise 1](../exercise-1).
- A Key Source that uses the branch key ID you configured
  in [Exercise 1](../exercise-1).
  When you created a branch key in Exercise 1,
  a beacon key that shared that same id was automatically generated.
  This is the key that will be used to calculate beacon values.

Then, configure a `SearchConfig` with this `BeaconVersion`.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5 -->
```java
  // BEGIN EXERCISE 2 STEP 5a
  public static ArrayList<CompoundBeacon> MakeCompoundBeacons() {
    ArrayList<CompoundBeacon> beacons = new ArrayList<CompoundBeacon>();
    beacons.add(MakeGsi1PartitionKey());
    beacons.add(MakeGsi1SortKey());
    return beacons;
  }

  public static BeaconKeySource MakeKeySource() {
    return BeaconKeySource.builder()
        .single(
            SingleKeyStore.builder()
                .keyId(BRANCH_KEY_ID)
                .cacheTTL(3600)
                .build())
        .build();
  }

  public static BeaconVersion MakeBeaconVersion() {
    return BeaconVersion.builder()
        .version(1)
        .keyStore(MakeKeyStore())
        .keySource(MakeKeySource())
        .standardBeacons(MakeStandardBeacons())
        .compoundBeacons(MakeCompoundBeacons())
        .build();
  }

  public static SearchConfig MakeSearchConfig() {
    ArrayList<BeaconVersion> versions = new ArrayList<BeaconVersion>();
    versions.add(MakeBeaconVersion());

    return SearchConfig.builder()
        .versions(versions)
        .writeVersion(1)
        .build();
  }
  // END EXERCISE 2 STEP 5a
```

:::
::::

Finally, add this Search Config to your DynamoDB Encryption Interceptor
configuration.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

<!-- !test check java step 5b -->
```java
        // BEGIN EXERCISE 2 STEP 5b
        .search(MakeSearchConfig())
        // END EXERCISE 2 STEP 5b
```

:::
::::

#### What Happened?

The client is now configured with searchable encryption
to support queries on the encrypted email attribute.

When the applications uses the AWS Database Encryption SDK
to encrypt it will:

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

### Checking Your Work

If you want to check your progress,
or compare what you've done versus a finished example, 
check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/workshop/java/adding-searchable-encryption-configuration-complete
```

:::
::::

## Try it Out

Now use the CLI to create the table that will back the Employee Portal Service
for this exercise.

<!-- !test program
cd ./workshop/java/exercise-2

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

[Go to the DynamoDB AWS Console to confirm that your expected table is created](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise2_Table).

Now that you have updated the code
to support this access pattern on encrypted data
you need to write the beacons to the database.

Before we get started, let's first reset the data in your table.

<!-- !test check load-data -->
```bash
./load-data
```

## Get Timecards by Email

Let's try querying timecards using the newly supported access pattern:

<!-- !test in get-tickets-author -->
```bash
./employee-portal get-tickets --author-email=zorro@gmail.com
```

Expected output:

<!-- !test out get-tickets-author -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
2                   2022-10-06T14:32:25      zorro@gmail.com     charlie@gmail.com   3           Easy Bug            This seems simple enough
1                   2022-10-07T14:32:25      zorro@gmail.com     able@gmail.com      3           Bad Bug             This bug looks pretty bad
```

Even though email is encrypted client-side, by utilizing
the beacons you configured, the client was able to retrieve the
correct records!

## Get Timecards by Email and Date

Because you configured the sort key for your new
Global Secondary Index to contain the ticket's creation date,
you can also specify start and end dates using the CLI.
With this command, the CLI will return all timecards
that were created on or after the `start` date and on or after the `end` date:

<!-- !test in get-tickets-author-start-end -->
```bash
./employee-portal get-tickets --author-email=zorro@gmail.com --start=2022-10-06T00:00:00 --end=2022-10-07T00:00:00 
```

Expected output:

<!-- !test out get-tickets-author-start-end -->
```
ticketNumber        modifiedDate             authorEmail         assigneeEmail       severity    subject             message
2                   2022-10-06T14:32:25      zorro@gmail.com     charlie@gmail.com   3           Easy Bug            This seems simple enough
```

While creation date is not encrypted in our table,
it can still be used in conjunction with the encrypted email field
to support this access pattern.

## Inspect your table DynamoDB side

Take a look at the [AWS Console for your DynamoDB table](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#table?name=Exercise2_Table).

Note that all of your data is still encrypted as it was in Exercise 1,
however there are a couple of additional fields calculated.

For every ticket item in your table, there are now attributes
for each of the beacons you configured.
The values of these new fields are the hex representations of
truncated HMACs.

## Explore Further

Want to dive into more content related to this exercise?
Try out these links.

* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html" target="_blank">AWS KMS: Key Grants</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html" target="_blank">AWS KMS: Key Policies</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policy-modifying-external-accounts.html" target="_blank">AWS KMS: Cross-account KMS Key Usage</a>
* <a href="https://aws.amazon.com/dynamodb/global-tables/" target="_blank">Amazon DynamoDB: global tables</a>

# Next exercise

Ready for more?
Next you will work on [adding another access pattern](../exercise-3)
to our searchable encryption configuration.

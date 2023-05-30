---
title : "Exercise 2"
weight : 200
---

# Exercise 2: Adding Searchable Encryption Configuration

In this section, you will configure the AWS Database Encryption SDK to use searchable encryption.

## Background

Your Employee Portal will encrypt items locally before you `put` them
into your table,
and will decrypt locally when you `get` items back from your table.
This process is called [client-side encryption](TODO).

This presents us with an interesting problem.
If your table only ever sees your data in encrypted form,
how can you ever effectively query on that encrypted data?

To accomplish this, the AWS Database Encryption SDK for DynamoDb
includes the [searchable encryption](TODO) feature,
which allows you to calculate and store
[beacons](TODO) alongside your data.
The client then utilizes these beacons whenever you make a query,
retrieving the correct encrypted data for your query.

Before you use [searchable encryption](TODO)
for your own use case, please read our [AWS documentation](TODO)
and ensure that [beacons are right for you](TODO).

To use [searchable encryption](TODO),
you need another kind of key, a beacon key.
This beacon key will be used during both write and query
in order to calculate the truncated HMACs that comprise beacons.

You will generate a new data key using AWS KMS
and store that data key, encrypted, in a new DynamoDB table.
This data key is called the beacon key
in order to distinguish this key from any other
AWS KMS data keys.
The beacon key is encrypted by
KMS Key that you set up in [Getting Started](./getting-started.md)
before being stored in DynamoDB.

Here you will explore one access pattern in detail,
as well as set the beacon key.

## Let's Go!

### Starting Directory

If you just finished [Adding the Database Encryption SDK](./adding-the-database-encryption-sdk.md), you are all set.

If you aren't sure, or want to catch up,
jump into the `adding-searchable-encryption-configuration` directory for the language of your choice.

=== "Java"

    ```bash 
    cd ~/environment/workshop/exercises/java/adding-searchable-encryption-configuration-start
    ```

### Step 1: Create the Keystore Table

=== "Java"

    ```{.java hl_lines="4"}
    ```

#### What Happened?

You just created a new DynamoDB table to store your beacon key.
This table also stores branch keys.
Branch keys are used by the AWS KMS Hierarchy Keyring.

### Step 2: Add a Branch key to the keystore

=== "Java"

    ```{.java hl_lines="3"}
    ```

#### What Happened?

You just created the branch key in the key store table.
You have added both a beacon key
as well as branch key.

Every branch key has beacon key associated with it for convenance.
You can only use the beacon key and ignore the branch key if you like.

### Step 3: Configure Searchable Encryption

=== "Java"

    ```{.java hl_lines="3"}
    ```

#### What Happened?

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

### Checking Your Work

If you want to check your progress,
or compare what you've done versus a finished example, 
check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

=== "Java"

    ```bash 
    cd ~/environment/workshop/exercises/java/adding-searchable-encryption-configuration-complete
    ```

## Try it Out

Now that you have updated the code
to support this access pattern on encrypted data
you need to write the beacons to the database.

Before we get started, let's first reset the data in your table.

```bash
./load_data
```


Experiment using the API as much as you like. 


=== "Java"

    ```java
        // Example of getting an employee record
        // Example of the next access pattern that should fail 
        // Example of adding a new record that we can find
    ```

## Explore Further

Want to dive into more content related to this exercise?
Try out these links.

* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/grants.html" target="_blank">AWS KMS: Key Grants</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html" target="_blank">AWS KMS: Key Policies</a>
* <a href="https://docs.aws.amazon.com/kms/latest/developerguide/key-policy-modifying-external-accounts.html" target="_blank">AWS KMS: Cross-account KMS Key Usage</a>
* <a href="https://aws.amazon.com/dynamodb/global-tables/" target="_blank">Amazon DynamoDB: global tables</a>


# Next exercise

Ready for more?
Next you will work [add the remaining access patterns](./adding-the-remaining-access-patterns.md)
to our searchable encryption configuration.

---
title : "Exercise 3"
weight : 300
---

# Exercise 3: Adding The Remaining Access Patterns

In this section, you will will configure searchable encryption
to configure the remaining access patterns.

## Background

In Exercise 2, you configured [searchable encryption](TODO)
to enable you to perform [TODO what is the access pattern?].
However, this is just one access pattern out of many we want to support.

In this exercise, you will add support for the remaining access patterns
originally supported in Exercise 1.

As you configure each new beacon to support a new access pattern,
consider what [truncation length is appropriate for that beacon](TODO)
as well as whether [beacons are right that access pattern in the first place](TODO).
As you go through the workshop, note that various
considerations on the tradeoffs being made between security and performance.

## Let's Go!

### Starting Directory

If you just finished [Adding Searchable Encryption Configuration](./adding-searchable-encryption-configuration.md),
you are all set.

If you aren't sure, or want to catch up,
jump into the `adding-the-remaining-access-patterns-start` directory for the language of your choice.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/exercises/java/adding-the-remaining-access-patterns-start
```

:::
::::

### Step 1: Set Encryption Context on Encrypt

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```{.java hl_lines="5 12 13"}
// Edit ./src/main/java/sfw/example/esdkworkshop/Api.java
...

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

...

    public PointerItem store(byte[] data, Map<String, String> context) {
    // ENCRYPTION-CONTEXT-START: Set Encryption Context on Encrypt
    CryptoResult<byte[], KmsMasterKey> encryptedMessage =
        awsEncryptionSdk.encryptData(mkp, data, context);
    DocumentBundle bundle =
        DocumentBundle.fromDataAndContext(encryptedMessage.getResult(), context);
// Save your changes
```

:::
::::

#### What Happened?

The Document Bucket `context` will now be supplied to the AWS Encryption SDK and AWS KMS as encryption context. If a non-empty key-value pair map is supplied to `store`, those key-value pairs will be used in encryption and decryption operations all the way through to KMS:

* The contents of `context` will appear in KMS audit logs.
* The contents of `context` will be availble to use in KMS Key Policies and Grants to make authorization decisions.
* The contents of `context` will be written to the Encryption SDK message.
* Supplying the exact-match contents of `context` will be required to decrypt any encrypted data keys.
* The contents of `context` will now be available on Decrypt to use in making assertions.

Next you will update `retrieve` to use the encryption context on decrypt.

### Step 2: Use Encryption Context on Decrypt

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```{.java hl_lines="3 4"}
// Edit ./src/main/java/sfw/example/esdkworkshop/Api.java and find retrieve(...)
    // ENCRYPTION-CONTEXT-START: Use Encryption Context on Decrypt
    Map<String, String> actualContext = decryptedMessage.getEncryptionContext();
    PointerItem pointer = PointerItem.fromKeyAndContext(key, actualContext);
// Save your changes
```

:::
::::

#### What Happened?

Now on decrypt, the validated encryption context from the Encryption SDK Message Format header will be passed back to the application. Any business logic that would benefit from using the encryption context data for making decisions can use the version bound and validated by the Encryption SDK and KMS.

Next you will add a mechanism for the application to test assertions made in encryption context before working with the returned data.

### Step 3: Making Assertions

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```{.java hl_lines="3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25"}
// Edit ./src/main/java/sfw/example/esdkworkshop/Api.java and find retrieve(...)
    // ENCRYPTION-CONTEXT-START: Making Assertions
    boolean allExpectedContextKeysFound = actualContext.keySet().containsAll(expectedContextKeys);
    if (!allExpectedContextKeysFound) {
        // Remove all of the keys that were found
        expectedContextKeys.removeAll(actualContext.keySet());
        String error =
        String.format(
            "Expected context keys were not found in the actual encryption context! "
            + "Missing keys were: %s",
            expectedContextKeys.toString());
        throw new DocumentBucketException(error, new NoSuchElementException());
    }
    boolean allExpectedContextFound =
        actualContext.entrySet().containsAll(expectedContext.entrySet());
    if (!allExpectedContextFound) {
        Set<Map.Entry<String, String>> expectedContextEntries = expectedContext.entrySet();
        expectedContextEntries.removeAll(actualContext.entrySet());
        String error =
            String.format(
                "Expected context pairs were not found in the actual encryption context! "
                + "Missing pairs were: %s",
                expectedContextEntries.toString());
        throw new DocumentBucketException(error, new NoSuchElementException());
    }
// Save your work
```

:::
::::

#### What Happened?

`retrieve` will use its "expected context keys" argument to validate that all of those keys (with any associated values) are present in the encryption context. `retrieve` will also use its "expected context" argument to validate that the exact key-value pairs specified in expected context are present in the actual encryption context. If either of those assumptions is invalid, `retrieve` will raise an exception before returning the data. These assertions safeguard against accidentally returning unintended, corrupted, or tampered data to the application.

Now the Document Bucket will use AWS KMS and the AWS Encryption SDK to ensure that the `context` metadata is consistent throughout the lifetime of the objects, resistant to tampering or corruption, and make the validated context available to the application logic to make additional business logic assertions safely.

### Checking Your Work

If you want to check your progress, or compare what you've done versus a finished example, check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
cd ~/environment/workshop/exercises/java/adding-the-remaining-access-patterns-complete
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

Congratulations! You have officially completed the Busy Engineer's Document Bucket workshop. Proceed to [Clean Up and Closing](./clean-up-and-closing.md) to tear down your workshop environment.

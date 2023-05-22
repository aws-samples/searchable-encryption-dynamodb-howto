# Exercise 3: Adding The Remaining Access Patterns

In this section, you will will configure searchable encryption
to configure the remaining access patterns.

## Background

You have one access pattern,
but you wanted to query.
There is a whole list of access patterns you want to use.

BUG BUG -- link to docs on warnings.



## Let's Go!

### Starting Directory

If you just finished [Adding Searchable Encryption Configuration](./adding-searchable-encryption-configuration.md),
you are all set.

If you aren't sure, or want to catch up,
jump into the `adding-the-remaining-access-patterns-start` directory for the language of your choice.

=== "Java"

    ```bash 
    cd ~/environment/workshop/exercises/java/adding-the-remaining-access-patterns-start
    ```

### Step 1: Set Encryption Context on Encrypt

=== "Java"

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


#### What Happened?

The Document Bucket `context` will now be supplied to the AWS Encryption SDK and AWS KMS as encryption context. If a non-empty key-value pair map is supplied to `store`, those key-value pairs will be used in encryption and decryption operations all the way through to KMS:

* The contents of `context` will appear in KMS audit logs.
* The contents of `context` will be availble to use in KMS Key Policies and Grants to make authorization decisions.
* The contents of `context` will be written to the Encryption SDK message.
* Supplying the exact-match contents of `context` will be required to decrypt any encrypted data keys.
* The contents of `context` will now be available on Decrypt to use in making assertions.

Next you will update `retrieve` to use the encryption context on decrypt.

### Step 2: Use Encryption Context on Decrypt

=== "Java"

    ```{.java hl_lines="3 4"}
    // Edit ./src/main/java/sfw/example/esdkworkshop/Api.java and find retrieve(...)
        // ENCRYPTION-CONTEXT-START: Use Encryption Context on Decrypt
        Map<String, String> actualContext = decryptedMessage.getEncryptionContext();
        PointerItem pointer = PointerItem.fromKeyAndContext(key, actualContext);
    // Save your changes
    ```

#### What Happened?

Now on decrypt, the validated encryption context from the Encryption SDK Message Format header will be passed back to the application. Any business logic that would benefit from using the encryption context data for making decisions can use the version bound and validated by the Encryption SDK and KMS.

Next you will add a mechanism for the application to test assertions made in encryption context before working with the returned data.

### Step 3: Making Assertions

=== "Java"

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

#### What Happened?

`retrieve` will use its "expected context keys" argument to validate that all of those keys (with any associated values) are present in the encryption context. `retrieve` will also use its "expected context" argument to validate that the exact key-value pairs specified in expected context are present in the actual encryption context. If either of those assumptions is invalid, `retrieve` will raise an exception before returning the data. These assertions safeguard against accidentally returning unintended, corrupted, or tampered data to the application.

Now the Document Bucket will use AWS KMS and the AWS Encryption SDK to ensure that the `context` metadata is consistent throughout the lifetime of the objects, resistant to tampering or corruption, and make the validated context available to the application logic to make additional business logic assertions safely.

### Checking Your Work

If you want to check your progress, or compare what you've done versus a finished example, check out the code in one of the `-complete` folders to compare.

There is a `-complete` folder for each language.

=== "Java"

    ```bash 
    cd ~/environment/workshop/exercises/java/adding-the-remaining-access-patterns-complete
    ```

## Try it Out

Now that you pass encryption context all the way through to KMS and validate it on return, what assertions do you want to make about your data?

Here's some ideas for things to test:

* Expecting exact match of key-value pairs for keys like `stage`, `shard`, and `source-fleet`
* Expecting a set of keys to be present like `submit-date` and `category`
* Expecting an exact match of a subset of the supplied key-value pairs (e.g. only `stage` and `shard`, not `source-fleet`)
* Doing the same for expected keys with any value
* Adding a constraint of a new key that you didn't supply at encryption time
* Adding a constraint with a different value, like `stage=production`
* Changing capitalization
* Using sorted versus unsorted mappings, such as `java.util.SortedMap<K, V>` in Java or `collections.OrderedDict` in Python

There's a few simple suggestions to get you started in the snippets below.

=== "Java"

    ```bash 
    // Compile your code
    mvn compile

    // To use the API programmatically, use this target to launch jshell
    mvn jshell:run
    /open startup.jsh
    import java.util.HashMap;
    Api documentBucket = App.initializeDocumentBucket();
    HashMap<String, String> context = new HashMap<String, String>();
    context.put("shard", "test");
    context.put("app", "document-bucket");
    context.put("origin", "development");
    documentBucket.list();
    PointerItem item = documentBucket.store("Store me in the Document Bucket!".getBytes(), context);
    DocumentBundle document = documentBucket.retrieve(item.partitionKey().getS(), context);
    System.out.println(document.getPointer().partitionKey().getS() + " : " + new String(document.getData(), java.nio.charset.StandardCharsets.UTF_8));
    // Ctrl+D to exit jshell

    // Or, to run logic that you write in App.java, use this target after compile
    mvn exec:java
    ```

## Explore Further

Encryption context can provide different types of features and guardrails in your application logic. Consider these ideas for further exploration:

* **Detecting Drift** - `context` contents are stored on the DynamoDB item. S3 has object metadata that could also use the `context` pairs. How would you use the validated encryption context to validate and guardrail those two data sources? What could that feature add to your application?
* **Meta-operations on Encryption Context** - the encryption context is stored on the <a href="https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/message-format.html" target="_blank">open-specification AWS Encryption SDK Message Format</a>. Would it help your system to write tools to process the metadata -- such as the encryption context -- on the message format?
* **DynamoDB Keys and Indexes** - the Document Bucket adds composite indexes by `context` key. What about adding composite keys by key-value pairs? If you know a particular key should always be present in well-formed encrypted data, perhaps that should also be a <a href="https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SecondaryIndexes.html" target="_blank">Secondary Index</a>?
* **Enforing EC Keys** - If you know that there is a key that should always be present, and that you want to index on in DynamoDB, do you want to enforce that it's always present? You can extend the <a href="https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/concepts.html#crypt-materials-manager" target="_blank">Cryptographic Materials Manager</a> component in the AWS Encryption SDK to enforce this during cryptographic operations.
* **Alarms and Monitoring** - How can you leverage encryption context and <a href="https://docs.aws.amazon.com/awscloudtrail/latest/userguide/cloudwatch-alarms-for-cloudtrail.html" target="_blank">CloudWatch Alarms for CloudTrail</a> to monitor and protect your application?

# Next exercise

Congratulations! You have officially completed the Busy Engineer's Document Bucket workshop. Proceed to [Clean Up and Closing](./clean-up-and-closing.md) to tear down your workshop environment.

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

### Step 1: Add the DB-ESDK Dependency

Look for `ADD-DB-ESDK-START` comments in the code to help orient yourself.

Start by adding the Database Encryption SDK dependency to the code.

=== "Java"

    ```{.java hl_lines="5 6 7 8 9 10"}
    ```


#### What Happened?

1. You added a dependency on the AWS Database Encryption SDK library in your code 

### Step 2: Add Encryption to the Dynamodb client

Now that you have the AWS Dynamodb Encryption SDK imported,
start using it encrypting your data.

=== "Java"

    ```{.java hl_lines="4 5 6"}
    ```

#### What Happened?

The application will use the AWS Database Encryption SDK
to encrypt your data client-side under a KMS Key before storing it by:

1. Requesting a new data key using your Keyring
1. Encrypting your configured attributes with the returned data key
1. Formatting a DynamodbDB item with the encrypted attributes.
1. Passing the encrypted item to the AWS DDB SDK for storage in DDB

When you get these items from DynamoDB
the application will also use the AWS Database Encryption SDK
to decrypt your data client side under a KMS Key before returning it by:

1. Getting the encrypted item from DynamoDB
1. Requesting the data key using your Keyring
1. Using the decrypted data key to decrypt the encrypted item
1. Returning the plaintext item to you

### Step 3: Configure the Faythe KMS Key in the AWS Database Encryption SDK

Now that you have declared your dependencies
and updated your code to encrypt and decrypt data,
the final step is to pass through the configuration
to the AWS Encryption SDK to start using your KMS Keys to protect your data.

=== "Java"

    ```{.java hl_lines="6 9 11"}
    ```

#### What Happened?

In [Getting Started](./getting-started.md), you launched CloudFormation stacks for KMS Keys.
One of these KMS Keys was nicknamed Faythe.
As part of launching these templates,
the KMS Key's Amazon Resource Name (ARN) was written to a configuration file on disk,
the `state` variable that is loaded and parsed.

Now Faythe's ARN is pulled into a variable,
and used to initialize a Keyring that will use the Faythe KMS Key.
That new Keyring/Master Key Provider is passed into your API,
and you are set to start encrypting and decrypting with KMS
and the Database Encryption SDK.

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


First, load up some test data into your portal!

```bash
./load-data
```

Similar to how we got and put records into the plaintext Employee Portal Service,
you can use the CLI to retrieve and put records into our Employee Portal Service
with client-side encryption.

### Retrieve items from your encrypted table

To start, let's retrieve all of our employees again:

[TODO do with "compare" command?]

```bash
./employee-portal get-employees
```

The data that the CLI prints will appear as plaintext
because you have set up the CLI to locally decrypt items as soon as they are retrieved from DynamoDB.

Let's verify that the data is actually encrypted in DynamoDB.
[TODO link to DynamoDB table].
You should see that [TODO what attrs are being encrypted]
now appear in DynamoDB as the `Bytes` DynamoDB type,
in an encrypted form.
You should also notice that there are two extra attributes written to our items.
`aws_dbe_head` contains our [material descrption](TODO),
which contains the metadata necessary for the AWS Database Encryption SDK
to understand how to decrypt the item.
`aws_dbe_foot` contains the signature calculated over our item.
[TODO describe the fields in the signature]

Your data is encrypted in dynamodb, but you've built the Employee Portal Service
such that this encryption and decryption happens transparently.

### Can we query?

In the previous step we just ran `get-employees` to get
all of our employees.
Under the hood, this [TODO].

We can verify that we are able to get a particular employee by primary key:

```bash
./employee-portal get-employees --employee-number=1234
```

However, what happens when we try to index on a different attribute?
Let's compare what our plaintext version of the Employee Portal Service
does with what you've just built

```bash
TODO
```

As you can see, because we did not set up GSIs on our new table yet,
our CLI is currently unable to retrieve employees by anything
other than their primary key value.

### Put items into your encrypted table

Let's double check putting new items into our table
via the CLI still behaves as expected.

Put a new ticket into our table:

```bash
./employee-portal put-ticket --ticket-number=<ticketNumber> --modified-date=<modifiedDate> --author-email=<authorEmail> --assignee-email=<assigneeEmail> --severity=<severity> --subject=<subject> --message=<message>
```

Now verify that this ticket appears in our table:

```bash
./employee-portal get-tickets
```

You may additionally want to verify that this item is encrypted as expected
in DynamoDB. [TODO link to DynamoDB table]

## Explore Further  --- BUG BUG

* **AWS Cloud Development Kit** - Check out the `~/environment/workshop/cdk` directory to see how the workshop resources are described using CDK.
* **Alice, Bob, and Friends** - <a href="https://en.wikipedia.org/wiki/Alice_and_Bob#Cast_of_characters" target="_blank">Who are Faythe and Walter?</a>

# Next exercise

Now that you are encrypting and decrypting items in the Employee Portal Service,
let's move onto adding back in those GSIs which enable all of our interesting access patterns.
Move onto the next exercise:
[Adding a searchable encryption configuration](./adding-searchable-encryption-configuration.md)?

# Exercise 1: Add the AWS Database Encryption SDK

In this section, you will add client-side encryption
to the example Employee Portal Service
using the AWS Database Encryption SDK and AWS KMS.

## Background

In [Getting Started](./getting-started.md),
you set up your Employee Portal Service environment
and selected a workshop language. 

Now you will add the AWS Database Encryption SDK to encrypt items on the client,
before they are transmitted off of the host machine to the internet.
You will use AWS KMS to provide a `data key` for each object,
using a KMS Key that you set up in [Getting Started](./getting-started.md).

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

Now that you have written the code code,
load it up and try it out.

If you'd like to try a finished example,
use your language's `-complete` directory as described above.

Load up some test data into your portal!

=== "Java"

    ```{.java hl_lines="5 8"}
    ```

Experiment using the API as much as you like. 

To get started, here are some things to try:

* Check the <a href="https://us-east-2.console.aws.amazon.com/cloudtrail/home?region=us-east-2#/events?EventSource=kms.amazonaws.com" target="_blank">CloudTrail Logs for usages of Faythe</a> when you encrypt the sample data
    * Note: Cloudtrail logs may take a couple minutes to appear in the console
* Take a look at the <a href="https://us-west-1.console.aws.amazon.com/dynamodbv2/home?region=us-west-1#service" target="_blank">contents of your DynamoDB table</a> to inspect the raw object

For more things to try, check out [Explore Further](#explore-further), below.

=== "Java"

    ```java

        // Example of adding employee records


    ```


## Explore Further  --- BUG BUG

* **AWS Cloud Development Kit** - Check out the `~/environment/workshop/cdk` directory to see how the workshop resources are described using CDK.
* **Alice, Bob, and Friends** - <a href="https://en.wikipedia.org/wiki/Alice_and_Bob#Cast_of_characters" target="_blank">Who are Faythe and Walter?</a>
* **Leveraging the Message Format** - The <a href="https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/message-format.html" target="_blank">AWS Encryption SDK Message Format</a> is an open standard. Can you write something to detect whether an entry in the Document Bucket has been encrypted in this format or not, and retrieve or decrypt appropriately?
* **More Test Content** - Small test strings are enough to get started, but you might be curious to see what the behavior and performance looks like with larger documents. What if you add support for loading files to and from disk to the Document Bucket?
* **Configuration Glue** - If you are curious how the Document Bucket is configured, take a peek at `~/environment/workshop/cdk/Makefile` and the `make state` target, as well as `config.toml` in the exercises root `~/environment/workshop/exercises/config.toml`. The Busy Engineer's Document Bucket uses a base <a href="https://github.com/toml-lang/toml" target="_blank">TOML</a> file to set standard names for all CloudFormation resources and a common place to discover the real deployed set. Then it uses the AWS Cloud Development Kit (CDK) to deploy the resources and write out their identifiers to the state file. Applications use the base TOML file `config.toml` to locate the state file and pull the expected resource names. And that's how the system bootstraps all the resources it needs!

# Next exercise

Now that you are encrypting and decrypting,
how about [adding a searchable encryption configuration](./adding-searchable-encryption-configuration.md)?

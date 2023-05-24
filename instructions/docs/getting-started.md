# Getting Started

## Workshop Details

In this workshop, you will add encryption and decryption features
to an example Employee Portal Service.
You will learn about some
real world AWS patterns for integrating [client-side encryption](TODO)
using [AWS Key Management Service (AWS KMS)](TODO)
and the [AWS Database Encryption SDK (DB-ESDK)](TODO) in application code.
You will additionally learn how to leverage [searchable encryption](TODO) using [beacons](TODO)
to enable you to continue to support the access patterns required by your use case.

TODO What is does the Employee Portal Service do?

By default, when you use DynamoDB your data is already protected with [server-side encryption](TODO).
However, you may want to have further control over the encryption and decryption of your data
by using [client-side encryption](TODO).

But introducing client-side encryption to database systems introduces a host of complex problems.
How should we encrypt sensitive data in our items in the first place?
What should we do if we need to create an index over an attribute that contains sensitive data?

The workshop exercises will help you address some of these challenges.

## Background

In this section, we will give you step-by-step instructions to prepare your AWS Environment
to build the Employee Portal Service.

To set up your environment, you will:

* TODO

## Let's Go!

### Important Note About Accounts

If you are using your own AWS Account for this workshop, make sure:

1. It is not a production account. This is a workshop for learning and experimentation. Don't put production at risk!
1. When you are done with the exercises, you follow the instructions in [Clean Up and Closing](./clean-up-and-closing.md) to clean up the deployed resources.

If you are working through these exercises in an AWS classroom environment, AWS accounts have been created for you.

### Procedure

TODO

1. Sign in to your AWS Account for the workshop

**Your environment is ready!** 


# Start the workshop!

Now that you have your environment and language selected, you can start [Adding the Database Encryption SDK](./adding-the-database-encryption-sdk.md).

---
title : "Getting Started"
weight : 10
---

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

By default, when you use DynamoDB your data is already protected with [server-side encryption](TODO).
However, you may want to have further control over the encryption and decryption of your data
by using [client-side encryption](TODO).

However, introducing client-side encryption to database systems introduces a host of complex problems.
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

### Set up the Employee Portal Service

TODO

1. Sign in to your AWS Account for the workshop

TODO: load data into DynamoDB

## What's next?

At this point, you should have an environment set up with a DynamoDB table
for the Employee Portal Service that you can interact with via a CLI.
Right now, the Employee Portal Service does not use any client-side encryption.

If you go to [your DynamoDB table](TODO),
you will see that your data is being served as plaintext.
While DynamoDB encrypts table data at rest
and your data is protected in transit via HTTPS,
you may want to encrypt your data such that its
plaintext is never available on DynamoDB servers.

This plaintext version of the Employee Portal Service will be used as a reference
as we build a version with client-side encryption in later steps.

Let's get familiar with how you can use this CLI to interact with data in your table.

To start, see what options are available:

```bash
./employee-portal help
```

This CLI contains commands to put new records in the database,
get records by various indexes, as well as scan the whole table.

### Getting records from the table

Let's try one of these commands out. Take a look at all of our current employees:

```bash
./employee-portal get-employees
```

This command should display all of the employee records currently in your table.
Can you think of any particular ways in which you might want to index on these employees?
Let's see what our options are for getting a particular set of employees:

```bash
./employee-portal get-employees help
```

This should display the additional argument you can use to specify which employees to return.
For example, to get all employees in Seattle, do:

```bash
./employee-portal get-employees --city=SEA
```

Now try to see if you can index the data in a different way.
Alternatively, explore the other data in our table.
What are our current meetings, projects, reservations, tickets, or timecards?

[TODO list out full set of available access patterns via get]

### Putting records into the table

You are now familiar with how to use the CLI to retrieve various data from
the Employee Portal Service.
Now let's see how to put data into the table.
Take a look at all the `put` commands available in the CLI:

```bash
./employee-portal help
```

Let's try to add a new meeting into our table.
To see what data we need to specify, do:

```bash
./employee-portal put-meeting help (TODO this is not really the _right_ way to do this, similar for other uses of help)
```

Now try specifying the required data so a new meeting can be added to your table.
For example, create a new meeting in your table:

[TODO fill out command data]

```bash
put-meeting --employee-number=<employeeNumber> --start=<startTime> --employee-email=<employeeEmail> --floor=<floor> --room=<room> --duration=<duration> --attendees=<attendees> --subject=<subject>
```

You should see the printed statement "Meeting Added" if the meeting was successfully added to your table.

Now, use the CLI's `get-meetings` to confirm that the meeting was actually added to your table.
See if you can get your newly added meeting by specifying `get-meetings` with a `--employee-email`.

# Start the workshop!

Now you should have a plaintext version of the Employee Portal Service
set up in your environment, and understand you to use the CLI
to get and put records into your DynamoDB table.

You can now start the first exercise:
[Adding the Database Encryption SDK](./adding-the-database-encryption-sdk.md).

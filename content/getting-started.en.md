---
title : "Getting Started"
weight : 10
---

# Getting Started

## Workshop Details

In this workshop, you will add [client-side encryption and decryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/client-server-side.html) features
to an example Employee Portal Service.
You will learn about some
real world AWS patterns for integrating [client-side encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/client-server-side.html)
using [AWS Key Management Service (AWS KMS)](https://docs.aws.amazon.com/kms/)
and the [AWS Database Encryption SDK (DB-ESDK)](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/) in application code.
You will additionally learn how to leverage [searchable encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/searchable-encryption.html) using [beacons](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/beacons.html)
to enable you to continue to support the access patterns required by your use case.

By default, when you use DynamoDB your data is already protected with [server-side encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/client-server-side.html).
However, you may want to have further control over the encryption and decryption of your data
by using [client-side encryption](https://docs.aws.amazon.com/database-encryption-sdk/latest/devguide/client-server-side.html).

Be aware, that introducing client-side encryption to database systems introduces a host of complex problems.
How should we encrypt sensitive data in our items in the first place?
What should we do if we need to create an index over an attribute that contains sensitive data?

The workshop exercises will help you address some of these challenges.

## Background

In this section, we will give you step-by-step instructions to prepare your AWS Environment
to build the Employee Portal Service.

To set up your environment, you will:

* Familiarize yourself with the Cloud9 development environment
* Add plaintext records to the table
* Retrieve plaintext records from the table

## Let's Go!

### Important Note About Accounts

If you are using your own AWS Account for this workshop, make sure:

1. It is not a production account. This is a workshop for learning and experimentation. Don't put production at risk!
1. When you are done with the exercises, you follow the instructions in [Clean Up and Closing](../clean-up-and-closing.md) to clean up the deployed resources.  Failure to clean up properly may cause you to incur charges on your AWS account.

If you are working through these exercises in an AWS classroom environment, AWS accounts have been created for you.

### Set up your development environment

1. Sign in to your AWS Account for the workshop. If you are working through these exercises in an AWS classroom environment, click the "Open AWS console" link in the sidebar.
2. Open Cloud9. A Cloud9 environment containing workshop resources has been created for you. If you are working through these exercises in an AWS classroom environment, open the [Event dashboard](https://catalog.us-east-1.prod.workshops.aws/event/dashboard/en-US), then copy and paste the "Cloud9IdeUrl" into your browser.

## Interact with your table

At this point, you should have a Cloud9 environment open
in the AWS account you will be using for the workshop.
The AWS account will have a DynamoDB table called `Plaintext_Table`.
If you go to [your DynamoDB table's items](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#item-explorer?table=Plaintext_Table&maximize=true),
you will see that it is empty.
You will now use the CLI to add items to the table.

### Putting records into the table

From your Cloud9 environment's terminal, access the `plaintext-base` folder:

```bash
cd ~/environment/workshop/java/plaintext-base
```

We provide a helper script to interact with the table, `employee-portal`. Take a look at all the `put` commands available in the CLI:

<!-- !test program
cd ./workshop/java/plaintext-base

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

<!-- !test check help -->
```bash
./employee-portal help
```

Let's try to add a new meeting into our table.
To see what data we need to specify, do:

<!-- !test check help put-meeting -->
```bash
./employee-portal help put-meeting
```

Now try specifying the required data so a new meeting can be added to your table.
For example, create a new meeting in your table:

<!-- !test check put-meeting -->
```bash
./employee-portal put-meeting --employee-number=1234 --start=2022-07-04T13:00 --employee-email=able@gmail.com --floor=12 --room=403 --duration=30 --attendees=SomeList --subject="Scan Beacons"
```

If you now go to [your DynamoDB table's items](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#item-explorer?table=Plaintext_Table&maximize=true),
you will see that it now contains one item with the arguments you provided.

We have provided a helper script to load more items into your table. Let's add them now:

<!-- !test check load-data -->
```bash
./load-data
```

If you now go to [your DynamoDB table's items](https://us-west-2.console.aws.amazon.com/dynamodbv2/home?region=us-west-2#item-explorer?table=Plaintext_Table&maximize=true),
you will see that it now contains more items.

## Getting records from the table

Let's use the CLI to access some of our data.
Take a look at all of our current employees:

<!-- !test in get-employees -->
```bash
./employee-portal get-employees
```

This command displays all of the employee records currently in your table.
You should see the following
<!-- !test out get-employees -->
```

WARNING : You are doing a full table scan. In real life, this would be very time consuming.

employeeNumber employeeEmail       managerEmail        name                title     location
1234           able@gmail.com      zorro@gmail.com     Able Jones          SDE9      {city=SEA, desk=3, floor=12, building=44, room=2}
2345           barney@gmail.com    zorro@gmail.com     Barney Jones        SDE8      {city=SEA, desk=4, floor=12, building=44, room=2}
3456           charlie@gmail.com   zorro@gmail.com     Charlie Jones       SDE7      {city=SEA, desk=5, floor=4, building=44, room=2}
4567           david@gmail.com     zorro@gmail.com     David Jones         SDE6      {city=NYC, desk=3, floor=1, building=22, room=2}
```

Can you think of any particular ways in which you might want to index on these employees?
Let's see what our options are for getting a particular set of employees:

```bash
./employee-portal help get-employees
```

This should display the additional argument you can use to specify which employees to return.
For example, to get all employees in New York, do:

<!-- !test in get-employees NYC -->
```bash
./employee-portal get-employees --city=NYC
```
You should see:
<!-- !test out get-employees NYC -->
```
employeeNumber employeeEmail       managerEmail        name                title     location
4567           david@gmail.com     zorro@gmail.com     David Jones         SDE6      {city=NYC, desk=3, floor=1, building=22, room=2}
```

Now try to see if you can index the data in a different way, e.g. getting all employees in New York City.

Alternatively, explore the other data in our table.
What are our current meetings, projects, reservations, tickets, or timecards?
Keep in mind the list of supported access patterns we will implement on our encrypted table:

1. Get employee info by email
1. Get employee info by employeeID
1. Get employees by manager email
1. Get employees by city.building.floor.desk

1. Get meetings by date and email
1. Get meetings by date and employeeID
1. Get meetings by date and building/floor/room
1. Get meetings by email

1. Get projects by status, start and target date
1. Get projects by name

1. Get reservations by email
1. Get reservations by building ID
1. Get reservations by building ID and time range

1. Get ticket by ticket ID
1. Get ticket by employee email
1. Get ticket by assignee email
1. Get tickets last modified in a date range

1. Get time cards by email
1. Get time cards by date range
1. Get time cards by role

If you have time, try out some of these access patterns on your plaintext table.

## What's next?

At this point, you should have a DynamoDB table
populated with multiple items
representing data for the Employee Portal Service
that you can interact with via a CLI
to `put` and `get` items.

You will see that all of these items are being served as plaintext.
Right now, the Employee Portal Service does not use any client-side encryption.
While DynamoDB encrypts table data at rest
and your data is protected in transit via HTTPS,
you may want to encrypt your data such that its
plaintext is never available on DynamoDB servers.

This plaintext version of the Employee Portal Service will be used as a reference
as we build a version with client-side encryption in later steps.

# Start the workshop!

Now that you have all of the prerequisite resources set up
and understand how to interact with them,
you can now start the first exercise:
[Adding the Database Encryption SDK](../exercise-1.md).

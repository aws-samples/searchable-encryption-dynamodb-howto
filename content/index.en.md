---
title : "The Busy Engineer's Database Encryption"
weight : 1
---

Welcome to the Busy Engineer's database encryption workshop.
In this workshop, you will be guided through adding client-side encryption
to your usage of Amazon DynamoDB
using the AWS Database Encryption SDK
and AWS Key Management Service.
Each exercise has step-by-step instructions.

The audience for this workshop are software engineers and product managers who are interested in protecting their own and their customer's data, while storing data in DynamoDB.  This workshop should take about 90 mins to complete.

## Background: The Employee Portal Service

This workshop is centered around an example Employee Portal Service.
This example service needs to keep track of employee data
and relate that employee data to various projects, meetings, timecards,
and buildings.

This example originates from [DynamoDB Deep Dive: Advanced Design Patterns](https://youtu.be/xfxBhvGpoa0?t=2293).
While not required to continue with this workshop,
this talk highlights the powerful ways you can use DynamoDB
to support a wide array of complex access patterns on a single table.
We will be using some of these advanced patterns, such as the
[Adjacency list design pattern](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/bp-adjacency-graphs.html#bp-adjacency-lists),
in this workshop.
Refer to the [AWS docs for Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
if you want to learn more.

Here is the entity relationship diagram for the Employee Portal Service.

![er-diagram](/static/er-diagram.svg)

<!-- ```plantuml alt="Data Model" title="Data Model"
@startuml

skinparam BackgroundColor transparent
' Changing the lines to white
' makes the diagram look better
' on dark backgrounds.

Employees }o-[#black]-o{ Projects
Employees }o-[#black]-o{ Meetings
Employees }o-[#black]o{ Tickets
Employees ||-[#black]-o{ Timecards
Projects ||-[#black]o{ Timecards
Tickets ||-[#black]-o{ Messages
Rooms ||-[#black]o{ Meetings
Buildings ||-[#black]o{ Employees
Buildings ||-[#black]-o{ Rooms

@enduml
``` -->

As you begin this workshop,
this system uses DynamoDB to store records for searching.
All records are stored in a single DynamoDB table
with global secondary indexes. 

See this diagram to understand the architecture of this system at the beginning of the workshop:

![beginning-architecture-diagram](/static/beginning-architecture-overview.svg)

<!-- ```plantuml alt="Beginning architecture overview" title="Beginning architecture overview"
@startuml

skinparam BackgroundColor transparent

!define AWSPuml https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/v15.0/dist
!include AWSPuml/AWSCommon.puml
!include AWSPuml/AWSSimplified.puml
!include AWSPuml/SecurityIdentityCompliance/all.puml


!include AWSPuml/Database/all.puml
!include AWSPuml/Storage/all.puml
!include AWSPuml/DeveloperTools/all.puml
!include AWSPuml/Groups/all.puml

DynamoDBItem(DDBItem, "Item", "")

AWSGroupColoring(Cloud9Instance, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(IDE, "Workshop Instance", #3B48CC, Cloud9, Cloud9Instance) {
  ToolsandSDKs(EmployeePortalService, "", "")
}

AWSGroupColoring(DDBService, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(DDBGroup, "Amazon DynamoDB", #3B48CC, DynamoDB, DDBService) {
  DynamoDBItems(DDBTable, "Table", "")
  DynamoDBGlobalsecondaryindex(GSI1, "GSI", "")
  DynamoDBGlobalsecondaryindex(GSI2, "GSI", "")

  NOTE: When uncommenting, remove the backslash from next 2 lines
  DDBTable -\-> GSI1
  DDBTable -\-> GSI2
}

EmployeePortalService <- DDBItem
DDBItem -> DDBGroup

@enduml
``` -->

There are a variety of access patterns that this Employee Portal Service
will support.
For example, you may want to get all the meetings booked by a particular employee.
Or you may want to get all of the tickets created within a certain time range.
A full list of supported access patterns is provided below:

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

For this workshop, you will be interacting with this Employee Portal Service
with a CLI that can retrieve this data via these access patterns.

At the start of this workshop, this Employee Portal Service,
supporting a wide variety of different access patterns,
is already built for you.

During the workshop, you will add client-side encryption
to this Employee Portal Service,
such that data is encrypted before it is sent to DynamoDB
and is decrypted locally once it is retrieved from DynamoDB. 
With client-side encryption, you will configure searchable
encryption so that you can maintain the original access patterns
supported by the Employee Portal Service.

See this diagram to understand the expected architecture of this system when the workshop is completed:

![final-architecture](/static/final-architecture.svg)

<!-- ```plantuml alt="Final architecture overview" title="Final architecture overview"
@startuml

skinparam BackgroundColor transparent

!define AWSPuml https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/v15.0/dist
!include AWSPuml/AWSCommon.puml
!include AWSPuml/AWSSimplified.puml
!include AWSPuml/SecurityIdentityCompliance/all.puml


!include AWSPuml/Database/all.puml
!include AWSPuml/Storage/all.puml
!include AWSPuml/DeveloperTools/all.puml
!include AWSPuml/Groups/all.puml


AWSGroupColoring(KMSService, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(AWSKMS, "AWS KMS", #3B48CC, KeyManagementService, KMSService)

IdentityAccessManagementDataEncryptionKey(DataKey, "DataKey", "")

AWSGroupColoring(EncryptedDDBItemColor, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(EncryptedItem, "EncryptedItem", #3B48CC, IdentityAccessManagementEncryptedData, EncryptedDDBItemColor) {
  DynamoDBItem(DDBItem, "Item", "")
}
IdentityAccessManagementDataEncryptionKey(DataKey, "", "")

AWSGroupColoring(Cloud9Instance, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(IDE, "Workshop Instance", #3B48CC, Cloud9, Cloud9Instance) {
  ToolsandSDKs(EmployeePortalService, "", "")
}

AWSGroupColoring(DDBService, #FFFFFF, #3B48CC, plain)
AWSGroupEntity(DDBGroup, "Amazon DynamoDB", #3B48CC, DynamoDB, DDBService) {
  DynamoDBItems(DDBTable, "Table", "")
  DynamoDBGlobalsecondaryindex(GSI1, "GSI", "")
  DynamoDBGlobalsecondaryindex(GSI2, "GSI", "")

  NOTE: When uncommenting, remove the backslash from next 2 lines
  DDBTable -\-> GSI1
  DDBTable -\-> GSI2
}

EmployeePortalService <- DDBItem
DDBItem -> DDBGroup

AWSKMS <-- DataKey
  NOTE: When uncommenting, remove the backslash from next line
DataKey -\-> EmployeePortalService

@enduml
``` -->

## Exercises

In this workshop, you will work through the following exercises:

1. Adding the AWS Database Encryption SDK
1. Start configuring searchable encryption for a single access pattern
1. Add searchable encryption support to access to Employee data
1. Add searchable encryption support for the remaining access patterns

## Getting Started

Ready to start? Proceed to [Getting Started](./getting-started.md) to begin.

# Searchable Encryption Demo

[AWS re:Invent 2021 - DynamoDB deep dive: Advanced design patterns](https://www.youtube.com/watch?v=xfxBhvGpoa0) presents a database used to run a business, tracking employees, meetings, projects and tickets. He takes six separate tables from an SQL database, and converts them into a single DynamoDB table with three Global Secondary Indexes.

**Question** : Can we do all of these things on an encrypted database
with beacons?

## Record Types

There are six record types in one table.

Attributes tagged with `^` are optional,
as they can be parsed out of the indexed attributes.
Keeping them makes things simpler,
omitting them makes things cheaper.

The `Location` field is a map, for example
`{"City" : "SEA", "Building" : "SEA22", "Floor" : 27, "Desk" : 625}`
or
`{"City" : "SEA", "Building" : "SEA22", "Floor" : 27, "Room" : 625}`


### Employee Record

one record per employee

* PK  - employee ID
* SK  - "E" + employee ID
* PK1 - employee email
* SK1  - "E" + employee ID
* PK2 - manager email
* PK3 - City
* SK3 - Building.floor.desk
* EmployeeID^
* EmployeeEmail^
* ManagerEmail^
* Location^
* EmployeeName
* EmployeeTitle

### Ticket Record

one record per ticket modification

* PK - ticket number
* SK - ticket modification timestamp
* PK1 - email of creator
* SK1 - ticket modification timestamp
* PK2 - email of assignee
* PK3 - Severity
* SK3 - ticket modification timestamp
* TicketNumber^
* TicketModTime^
* CreatorEmail^
* AssigneeEmail^
* Severity^
* Subject
* Message

### Employee Meeting Record

one record for every (employee, meeting) pair

* PK - employee ID
* SK - start time of meeting + floor.room
* PK1 - employee email
* SK1 - start time of meeting + floor.room
* EmployeeID^
* EmployeeEmail^
* MeetingStart^
* Location^
* Duration - duration of zero has some special meaning?
* Attendees - list of employee email
* Subject

### Project Record

* PK - project name
* SK - project name
* PK1 - status
* SK1- project start date
* ProjectName^
* ProjectStatus^
* ProjectStart^
* Description
* ProjectTarget

### Time Card

one record per employee per project per week

* PK - project name
* SK - date for start of period + employee email
* PK1 - employee email
* ProjectName^
* EmployeeEmail^
* TimeCardStart^
* Hours
* Role

### Meeting Reservation Record

one record per meeting

* PK - building ID
* SK - start time + floor + room
* PK1 - organizer email
* SK1 - start time + floor + room
* Location^
* MeetingStart^
* OrganizerEmail^
* Duration - duration of zero has some special meaning?
* Attendees - list of attendee email
* Subject


## Required Access Patterns

1. Get meetings by date and email
1. Get meetings by date and employeeID
1. Get meetings by date and building/floor/room
1. Get employee data	by email
1. Get meetings by email
1. Get tickets by email
1. Get reservations by email
1. Get time cards by email
1. Get employee Info by employeeID
1. Get employee Info by email
1. Get Ticket History by ticket ID
1. Get Ticket History by employee email
1. Get Ticket History by assignee email
1. Get employees by city.building.floor.desk
1. Get employees by manager email
1. Get assigned tickets by assignee email
1. Get Tickets last touched in the past 24 hours
1. Get Projects by status, start and target date
1. Get Projects by name
1. Get Project History by date range
1. Get Project History by role
1. Get Reservations by BuildingID
1. Get Reservations by BuildingID and time range

## Schema

### Main Table (Indexed on PK and SK)

 * Employee Record : PK=EmployeeID, SK="E" + EmployeeID
 * Ticket Record : PK=TicketNumber, SK=TicketModTime
 * Employee Meeting Record : PK=EmployeeID, SK=MeetingStart + floor.room
 * Project Record : PK=ProjectName, SK=ProjectName
 * Time Card : PK=ProjectName, SK=TimeCardStart + EmployeeEmail
 * Meeting Reservation Record : PK=building ID, SK=MeetingStart + floor.room

### GSI-1 (Indexed on PK1 and SK1)

 * Employee Record : PK1=EmployeeEmail, SK1=`E`
 * Ticket Record : PK1=CreatorEmail, SK1=TicketModTime
 * Employee Meeting Record : PK1=EmployeeEmail, SK1=MeetingStart
 * Project Record : PK=ProjectStatus, SK1=ProjectStart
 * Time Card : PK= EmployeeEmail, SK=TimeCardStart
 * Meeting Reservation Record : PK=OrganizerEmail, SK=MeetingStart

### GSI-2 (Indexed on PK2 and SK)

 * Employee Record : PK2=ManagerEmail
 * Ticket Record : PK2=AssigneeEmail

### GSI-3 (Indexed on PK3 and SK3)

 * Employee Record : PK3=City, SK3=Building.floor.desk
 * Ticket Record : PK3=Severity, SK3=TicketModTime


| # | Access Pattern | Index | Key Condition | Filter Condition |
|:----------|:----------|:----------|:----------|:----------|
|1| Get meetings by date and email | GSI-1 | PK1=email SK1 between(date1, date2) | duration > 0 |
|2| Get meetings by date and employeeID | Table | PK=employeeID SK between(date1, date2) | duration > 0 |
|3| Get meetings by date and building/floor/room | Table | PK=buildingID SK between(date1, date2) | SK contains building.floor.room |
|4| Get employee data by email | GSI-1 | PK1=email SK1 > 30 days ago |    |
|5| Get meetings by email |GSI-1 | PK1=email SK1 > 30 days ago |    |
|6| Get tickets by email | GSI-1 | PK1=email SK1 > 30 days ago |    |
|7| Get reservations by email | GSI-1 | PK1=email SK1 > 30 days ago |    |
|8| Get time cards by email | GSI-1 | PK1=email SK1 > 30 days ago |    |
|9| Get employee Info by employeeID | Table | PK=employeeID SK starts_with("E") |    |
|10| Get employee Info by email | GSI-1 | PK1=email |SK starts_with("E")|
|11| Get Ticket History by ticket ID | Table | PK=TicketID |    |
|12| Get Ticket History by employee email | GSI-1 | PK1 = email |PK=TicketID|
|13| Get Ticket History by assignee email | GSI-2 | PK2 = email |PK=TicketID|
|14| Get employees by city.building.floor.desk | GSI-3 | PK3=city SK3 starts_with(building.floor.desk) |    |
|15| Get employees by manager email | GSI-2 | PK2=email SK > 3 |    |
|16| Get assigned tickets by assignee email | GSI-2 | PK2=email |    |
|17| Get Tickets last touched in the past 24 hours | GSI-3 | PK3=Priority SK3>yesterday |    |
|18| Get Projects by status, start and target date | GSI-1 | PK1=Status SK1 > StartDate |targetDelivery < targetDate|
|19| Get Projects by name | Table | PK=ProjectName SK=ProjectName |    |
|20| Get Project History by date range | Table | PK=ProjectName SK between(date1, date2) |    |
|21| Get Project History by role | Table |PK=ProjectName|role=roleName|
|22| Get Reservations by BuildingID | Table |PK=BuildingID|    |
|23| Get Reservations by BuildingID and time range | Table |PK=BuildingID SK between(date1, date2)|duration>0|

## Changes for Encryption

We will assert that dates and times can be plain text, but all other data needs to be encrypted. If we require encrypting the dates and times then this example becomes untenable, as most queries use date ranges.

### Add Beacons

Each field is encrypted, and so no good for searching; therefore,
we add beacons upon which we can search.

The fields marked with `^` which were optional are now required.
The indexed fields (e.g. PK, SK2) are now beacons,
but have no plaintext form. That is, PK and friends are NOT
configured as fields, encrypted or not.

The only non-index fields used for searching are Duration and Role.
For Duration, the only use is "Duration > 0".
For encryption we will not write the Duration field when it is zero,
and then test for the existence of the encrypted attribute.
`Role` is only matched for equality, so a Standard Beacon will suffice.

Beacons are specifically designed to produce false positives,
so after any search, after the records are decompressed, each
record must be examined to see if it really did match the query, and if
not, that result should be discarded. This is all handled by the ESDK,
but it does impose costs in both seconds and dollars.

### GSI-0

The key schema for the main table has fields that we want to encrypt, but we can’t encrypt primary keys. Thus we create a new index GSI-0, that holds exactly what the unencrypted main index held.
The main table will have no sort key. its partition key will be an HMAC of the (unencrypted) PK and SK fields. Unfortunately, this means that all of the
table's data is replicated in GSI-0, but this cannot be helped.

### Beacon Configuration

This assumes you've read documentation on beacon configuration elsewhere.

All of the index beacons are of necessity Compound Beacons.

All constructor parts are required.

#### PK Parts
 * EmployeeID  E-
 * TicketNumber T-
 * ProjectName P-
 * Building B- Location.Building

#### PK Constructors
 * EmployeeID
 * TicketNumber
 * ProjectName

#### SK Parts
 * EmployeeID  E-
 * TicketModTime M-
 * MeetingStart S-
 * Floor F- Location.Floor
 * Room R- Location.Room
 * ProjectName P-
 * TimeCardStart T-
 * EmployeeEmail EE-

#### SK Constructors
 * EmployeeID
 * TicketModTime
 * MeetingStart, Floor, Room
 * ProjectName
 * TimeCardStart, EmployeeEmail

#### PK1 Parts
 * EmployeeEmail EE-
 * CreatorEmail CE-
 * ProjectStatus S-
 * OrganizerEmail OE-

#### PK1 Constructors
 * CreatorEmail
 * EmployeeEmail
 * ProjectStatus
 * OrganizerEmail

#### SK1 Parts
 * EmployeeID  E-
 * TicketModTime M-
 * MeetingStart S-
 * Floor F- Location.Floor
 * Room R- Location.Room
 * ProjectStart P-

#### SK1 Constructors
 * EmployeeID
 * TicketModTime
 * MeetingStart, Floor, Room
 * ProjectStart

#### PK2 Parts
 * ManagerEmail ME-
 * AssigneeEmail  AE-

#### PK2 Constructors
 * ManagerEmail
 * AssigneeEmail

#### There is no SK2

#### PK3 Parts
 * City C- Location.City
 * Severity  S-

#### PK3 Constructors
 * City
 * Severity

#### SK3 Parts
 * Building B- Location.Building
 * Floor F- Location.Floor
 * Desk D- Location.Desk
 * TicketModTime M-

#### SK3 Constructors
 * Building, Floor, Desk
 * TicketModTime

# Searchable Encryption Demmo

[AWS re:Invent 2021 - DynamoDB deep dive: Advanced design patterns](https://www.youtube.com/watch?v=xfxBhvGpoa0) presents a database used to run a business, tracking employees, meetings, projects and tickets. He takes six separate tables from an SQL database, and convertes them into a single DynamoDB table with three Global Secondary Indexes.

## Record Types

### Employee Record

one record per enployee

* PK  - employee number
* SK  - employee tag : E1234
* PK1 - employee email
* PK2 - manager email
* PK3 - City
* SK3 - Building.floor.aisle.desk
* Name
* Title

### Ticket Record

one record per ticket modification

* PK - ticket number
* SK - ticket modification timestamp
* PK1 - email of creator
* PK2 - email of assignee
* PK3 - Severity
* SK3 - ticket modification timestamp
* Subject
* Message

### Employee Meeting Record

one record for every (employee, meeting) pair

* PK - employee ID
* SK - start time of meeting + floor.room
* PK1 - employee email
* Duration - duration of zero has some special meaning?
* Attendees - list of employee email
* Subject

### Project Record

* PK - project name
* SK - project name
* PK1 - status
* SK1- project start date
* Description
* Target delivery date

### Time Card

one record per employee per project per week

* PK - project name
* SK - date for start of period + employee email
* PK1 - employee email
* Hours
* Role

### Meeting Reservation Record

one record per meeting

* PK - building ID
* SK - start time + floor + room
* PK1 - organizer email
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
1. Get Ticket History by asignee email
1. Get employees by city.building.floor.aisle.desk
1. Get employees by manager email
1. Get assigned tickets by asignee email
1. Get Tickets last touched in the past 24 hours
1. Get Projects by status, start and target date
1. Get Projects by name
1. Get Project History by date range
1. Get Project History by role
1. Get Reservations by BuildingID
1. Get Reservations by BuildingID and time range

## Schema

### Main Table (Indexed on PK and SK)

 * Employee Record : PK=employee ID, SK=employee tag
 * Ticket Record : PK=ticket number, SK=ticket modification timestamp
 * Employee Meeting Record : PK=employee ID, SK=start time of meeting + floor.room
 * Project Record : PK=project name, SK=project name
 * Time Card : PK= project name, SK=date for start of period + employee email
 * Meeting Reservation Record : PK=building ID, SK=start time + floor.room

### GSI-1 (Indexed on PK1 and SK1)

 * Employee Record : PK1=employee email, SK1=`E`
 * Ticket Record : PK1=creator email, SK1=ticket modification timestamp
 * Employee Meeting Record : PK1=employee email, SK1=start time of meeting
 * Project Record : PK=status, SK1=project start date
 * Time Card : PK= employee email, SK=date for start of period
 * Meeting Reservation Record : PK=organizer email, SK=start time

### GSI-2 (Indexed on PK2 and SK)

 * Employee Record : PK2=manager email
 * Ticket Record : PK2=assigneee email

### GSI-3 (Indexed on PK3 and SK3)

 * Employee Record : PK3=City, SK3=Building.floor.aisle.desk
 * Ticket Record : PK3=Severity, SK3=ticket modification timestamp


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
|13| Get Ticket History by asignee email | GSI-2 | PK2 = email |PK=TicketID|
|14| Get employees by city.building.floor.aisle.desk | GSI-3 | PK3=city SK3 starts_with(building.floor.aisle.desk) |    |
|15| Get employees by manager email | GSI-2 | PK2=email SK > 3 |    |
|16| Get assigned tickets by asignee email | GSI-2 | PK2=email |    |
|17| Get Tickets last touched in the past 24 hours | GSI-3 | PK3=Priority SK3>yesteray |    |
|18| Get Projects by status, start and target date | GSI-1 | PK1=Status SK1 > startdate |targetDelivery < targetDate|
|19| Get Projects by name | Table | PK=ProjectName SK=ProjectName |    |
|20| Get Project History by date range | Table | PK=ProjectName SK between(date1, date2) |    |
|21| Get Project History by role | Table |PK=ProjectName|role=roleName|
|22| Get Reservations by BuildingID | Table |PK=BuildingID|    |
|23| Get Reservations by BuildingID and time range | Table |PK=BuildingID SK between(date1, date2)|duration>0|

## Changes for Encryption

We will assert that dates and times can be plain text, but all other data needs to be encrypted. If we require encrypting the dates and times then this example becomes untenable, as most queries use date ranges.

### Add Beacons

Each field is encrypted, and so no good for searching.
Alongside each field, we write a new field, called a beacon.
A beacon is a truncated hash (HMAC) of the field's contents.
These hashes are suitable only for exact match searches; however,
using compound beacons (see below) we can leave some plaintext and/or
some structure, that can be used with more complex query operators.

Specifially, for each encrypted field `X` we also write a new field `gZ_b_X` holding the beacon. Indexes cannot be built on encrypted fields so, for example,
GSI-3 for the encrypted database will be built on `gZ_b_PK3` and `gZ_b_SK3`.

Beacons are specifically designed to produce false positives,
so after any search, after the records are decompressed, each
record must be examined to see if it really did match the query, and if
not, that result should be discarded.

### GSI-0

The key schema for the main table has fields that we want to encrypt, but we can’t encrypt search keys. Thus we create a new index GSI-0, that holds exactly what the unencrypted main table held.
The main table will have no sort key. it’s partition key will be an HMAC of the (unencrypted) PK and SK fields.

### Beacon Configuration

If beacon configuration specifies a `prefix` character,
the part of the string preceeding this character is kept, unencrypted,
in the beacon value. e.g. `2022-10-28~ActualData` might become `2022-10-28~423`

If beacon configuration specifies a `split` character, the unencrypted
string is split on that character, a beacon value is calculated for each piece,
and the pieces are re-joind on that character, e.g. `SEA33.12.402` might become `.99.42.86.`

These characters must be chosen with care.
In this example `:` can't be the prefix character,
because some of the prefixes contain timestamps, which contain `:`.

Specially SK, SK1 and SK3 are configured with a `prefix` of `~` and a `split` of `.`.

*Employee Tag* in the original example is something like `E1234`,
and is selected via `starts_with(“E”)`.
To capture this in a beacon, we chage it to E~1234,
so that the beacon will be something like `E~42`

*Date:building.floor.room* : uses both the prefix and the split, so that
`2022-07-04~SEA33.12.402 ` might become `2022-07-04~.99.42.86.`

### Miscellaneous

*Duration* is used for filtering, but only with duration > 0. We will no longer write the duration field if it is zero, so we can instead test with `attribute_exists(duration)`




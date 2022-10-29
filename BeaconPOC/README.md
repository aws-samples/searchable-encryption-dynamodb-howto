# Beacons : Proof of Concept

Can one use beacons to implement Searchable Encryption for DynamoDB,
and support real world complex use cases?

Spoiler Alert : Yes.

[This demo](Demo.md) starts with a talk from AWS re:Invent 2021 on
advanced DynamoDB design patterns, pins down the details,
and then describes how one would achieve the same results on an
encrypted database using beacons. It defines table and index layouts,
as well as 23 queries that cover all of the necessary use cases.

The [text](./text) directory holds tsv files with the data
we want to import into our database.

The [rust](./rust) directory holds a simple program that translates
the files in the [text](./text) director into JSON, suitable
for import into DynamoDB.
One copy is written into [plain_json](./plain_json),
holding the normal plaintext data you would expect. 
Another copy is written into [encrypted_json](./encrypted_json),
holding encrypted data fields, plus beacons for each.


If you want to reproduce the results, use the following steps.

1. start [DynamoDBLocal](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

1. `make create_databases` : this will create two DynamoDB tables,
one plain one encrypted, each with several Global Secondary Indexes.
This takes several minutes to complete.

1. `make build` : this builds the rust program that converts then
text to JSON.

1. `make text` : this runs the program and create the JSON

1. `make import` : load the JSON into DynamoDB

1. `make check` : runs the set of 23 queries on both the plaintext
and encrypted tables, and ensures that they return the same number of records.

If you want to work against the real DynamoDB, instead use `make create_databases_remote`, `make import_remote` and `make check_remote`.
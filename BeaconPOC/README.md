# Beacons : Proof of Concept

Can one use beacons to implement Searchable Encryption for DynamoDB,
and support real world complex use cases?

Spoiler Alert : Yes on can.

[Sildes](./Overview.pptx) from a brief high-level overview are available.

[This demo](Demo.md) starts with a talk from AWS re:Invent 2021 on
advanced DynamoDB design patterns, pins down the details,
and then describes how one would achieve the same results on an
encrypted database using beacons. It defines table and index layouts,
as well as 23 queries that cover all of the necessary use cases.

The [text](./text) directory holds tsv files with the data
we want to import into our database.

The [rust](./rust) directory holds a simple program that translates
the files in the [text](./text) directory into JSON, suitable
for import into DynamoDB.
One copy is written into [plain_json](./plain_json),
holding the normal plaintext data you would expect. 
Another copy is written into [encrypted_json](./encrypted_json),
holding encrypted data fields, plus beacons for each encrypted field which
needs to be involved in searching.


## What this is NOT

### This is NOT a guide to proper beacon security.

 * There is no mention of proper truncation of the beacons
 * The SK and SK1 fields are nearly identical, which is bad for security
 * Some values, e.g. "Role", probably have too few unique values for reasonable beacon security.

### This is NOT a full implementation

 * It uses regular hashes, not HMACs
 * It hard codes beacon values into queries
 * It skips the post-query, client-side filtering necessary to remove false positives.

**This is ONLY a proof of concept that querying can do what needs to be done.**
## To run the demo

### Modify the data

1. Modify files in [text](./text) directory

1. `make build` : this builds the rust program that converts then
text to JSON.

1. `make text` : this runs the program and create the JSON

1. Examine results in [plain_json](./plain_json) and [encrypted_json](./encrypted_json).

### Reproduce the results

1. start [DynamoDBLocal](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

1. `make create_databases` : this will create two DynamoDB tables,
one plain one encrypted, each with several Global Secondary Indexes.

1. `make import` : load the JSON into DynamoDB

1. `make check` : runs the set of 23 queries on both the plaintext
and encrypted tables, and ensures that they return the same number of records.

If you want to work against the real DynamoDB, instead use `make create_databases_remote`, `make import_remote` and `make check_remote`.



# Aws-workshop-template

## Repo structure

```bash
.
├── contentspec.yaml                  <-- Specifies the version of the content
├── README.md                         <-- This instructions file
├── static                            <-- Directory for static assets to be hosted alongside the workshop (ie. images, scripts, documents, etc) 
└── content                           <-- Directory for workshop content markdown
    └── index.en.md                   <-- At the root of each directory, there must be at least one markdown file
    └── introduction                  <-- Directory for workshop content markdown
        └── index.en.md               <-- Markdown file that would be render 
```

## What's Included

This project contains the following folders:
* `static`: This folder contains static assets to be hosted alongside the workshop (ie. images, scripts, documents, etc) 
* `content`: This is the core workshop folder. This is generated as HTML and hosted for presentation for customers.

## How to create content

Under the `content` folder, Each folder requires at least one `index.<lang>.md` file. The file will have a header

```aidl
+++
title = "AWS Workshop Template"
weight = 0
+++
```

The title will be the title on navigation panel on the left. The weight determines the order the page appears in the navigation panel.

## How to test content

There are 2 ways to test this content.
Either locally or in a Workshop studio test event.

### Testing locally

First make sure you have DynamoDB-Local installed.
This will download the latest version of DDB-Local
and put it in the expected location for the tests.
```bash
make get_ddb_local
```

Next, you need to have some way to get credentials.
These credentials MUST have AWS KMS access to
`GenerateDataKey`
`Decrypt`
`Encrypt`
`GenerateDataKeyWithoutPlaintext`

The default KMS key stored in the repo is for testing ONLY!
It is a public KMS key used for test vectors in the AWS Encryption SDK.

Now to run the tests use the following:

```bash
make test_local; make start_ddb_local 
```

1. This will set the evn variable `USE_DDB_LOCAL`
1. Start the DDB-local process in the background
1. Run `txm` in the background to test the markdown content
1. Stop the started background DDB-local process

Some useful commands for testing with DDB-Local
```bash
aws dynamodb list-tables --endpoint-url http://localhost:8000
aws dynamodb scan --table-name BranchKey_Table --endpoint-url http://localhost:8000
aws dynamodb scan --table-name Exercise1_Table --endpoint-url http://localhost:8000
aws dynamodb describe-table --table-name Exercise1_Table --endpoint-url http://localhost:8000
```

When testing locally,
`test_local` updates the workshop in place.
To make this repeatable it will git reset the workshop.
If you are working on the code remember to commit your test first.

### Testing in Workshop Studio

First you need to create a test event.
You should wait ~20 min to log into the Cloud9 desktop.
It takes 5-10 min to spin up the instance,
but it takes another 5-10 min
for the System Manager Document to bootstrap the document.

Then you can start the workshop
and download the markdown files.
See the link at the end of `tips-and-troubleshooting`.

Once you have downloaded the content
make sure you have DynamoDB-Local installed.
This will download the latest version of DDB-Local
and put it in the expected location for the tests.
```bash
make get_ddb_local
```

Then you can run the tests:
```bash
make markdown_test
```

This will create the DDB tables,
so it can not be run multiple times.

### Updating the tests

See `txm` [here](https://www.npmjs.com/package/txm).

Any code block that you want to test
add a directive above it:
`<!-- !test check test-name-here -->`

This will run the closest
`<!-- !test program` above this code block.
You can look at many examples in the code.

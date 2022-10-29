#!/bin/bash -eu

if (($# == 1)); then
    if [[ "$1" == "local" ]]; then
	remote='--endpoint-url http://localhost:8000'
    elif [[ "$1" == "remote" ]]; then
	remote=''
    else
	echo 1>&2 "USAGE <program> local|remote"
	exit 1
    fi
else
    echo 1>&2 "USAGE <program> local|remote"
    exit 1
fi

function wait_active {
    while (($(aws dynamodb describe-table $remote --table-name $1 | fgrep ACTIVE | wc -l) == 0)); do
	echo waiting for index $1 to be active
	sleep 1
    done
    while (($(aws dynamodb describe-table $remote --table-name $1 | fgrep '"IndexStatus": "CREATING"' | wc -l) != 0)); do
        echo waiting for index to be ready on $1
        sleep 1
    done
}

echo creating table DemoEncrypted
aws dynamodb create-table $remote --table-name DemoEncrypted \
    --attribute-definitions AttributeName=MainKey,AttributeType=B \
    --key-schema AttributeName=MainKey,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 > /dev/null

wait_active DemoEncrypted
echo creating table GSI-0 on DemoEncrypted
aws dynamodb update-table $remote --table-name DemoEncrypted \
     --attribute-definitions AttributeName=gZ_b_PK,AttributeType=S \
          AttributeName=gZ_b_SK,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-0",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"gZ_b_PK", "KeyType":"HASH"},{"AttributeName":"gZ_b_SK", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

wait_active DemoEncrypted
echo creating table GSI-1 on DemoEncrypted
aws dynamodb update-table $remote --table-name DemoEncrypted \
     --attribute-definitions AttributeName=gZ_b_PK1,AttributeType=S \
          AttributeName=gZ_b_SK1,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-1",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"gZ_b_PK1", "KeyType":"HASH"},{"AttributeName":"gZ_b_SK1", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

wait_active DemoEncrypted
echo creating table GSI-2 on DemoEncrypted

aws dynamodb update-table $remote --table-name DemoEncrypted \
     --attribute-definitions AttributeName=gZ_b_PK2,AttributeType=S \
           AttributeName=gZ_b_SK,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-2",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"gZ_b_PK2", "KeyType":"HASH"},{"AttributeName":"gZ_b_SK", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

wait_active DemoEncrypted
echo creating table GSI-3 on DemoEncrypted

aws dynamodb update-table $remote --table-name DemoEncrypted \
     --attribute-definitions AttributeName=gZ_b_PK3,AttributeType=S \
           AttributeName=gZ_b_SK3,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-3",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"gZ_b_PK3", "KeyType":"HASH"},{"AttributeName":"gZ_b_SK3", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

echo DemoEncrypted ready to go

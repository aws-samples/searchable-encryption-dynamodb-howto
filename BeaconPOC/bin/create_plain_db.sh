#!/bin/bash -eu

function wait_active {
    while (($(aws dynamodb describe-table --table-name $1 | fgrep ACTIVE | wc -l) == 0)); do
	echo waiting for index $1 to be active
	sleep 1
    done
    while (($(aws dynamodb describe-table --table-name $1 | fgrep '"IndexStatus": "CREATING"' | wc -l) != 0)); do
        echo waiting for index to be ready on $1
        sleep 5
    done
}

echo creating table DemoPlain
aws dynamodb create-table --table-name DemoPlain \
    --attribute-definitions AttributeName=PK,AttributeType=S \
         AttributeName=SK,AttributeType=S \
    --key-schema AttributeName=PK,KeyType=HASH \
	 AttributeName=SK,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 > /dev/null

wait_active DemoPlain
echo creating table GSI-1 on DemoPlain
aws dynamodb update-table --table-name DemoPlain \
     --attribute-definitions AttributeName=PK1,AttributeType=S \
          AttributeName=SK1,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-1",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"PK1", "KeyType":"HASH"},{"AttributeName":"SK1", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

wait_active DemoPlain
echo creating table GSI-2 on DemoPlain

aws dynamodb update-table --table-name DemoPlain \
     --attribute-definitions AttributeName=PK2,AttributeType=S \
           AttributeName=SK,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-2",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"PK2", "KeyType":"HASH"},{"AttributeName":"SK", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

wait_active DemoPlain
echo creating table GSI-3 on DemoPlain

aws dynamodb update-table --table-name DemoPlain \
     --attribute-definitions AttributeName=PK3,AttributeType=S \
           AttributeName=SK3,AttributeType=S \
     --global-secondary-index-updates '[{"Create": {
       "IndexName": "GSI-3",
       "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5},
       "KeySchema": [{"AttributeName":"PK3", "KeyType":"HASH"},{"AttributeName":"SK3", "KeyType":"RANGE"}],
       "Projection":{"ProjectionType":"ALL"}
       }}]' > /dev/null

echo DemoPlain ready to go

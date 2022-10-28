#!/bin/bash -eu

echo 1 meetings by date and email
aws dynamodb query --table DemoEncrypted --index-name GSI-1 \
    --key-condition-expression "gZ_b_PK1 = :pk1 AND gZ_b_SK1 between :sk1a AND :sk1b" \
    --expression-attribute-values '{":pk1":{"S":"824"},":sk1a":{"S":"2022-07-03"}, ":sk1b":{"S":"2022-07-05"}}'

echo 2 meetings by date and email
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND gZ_b_SK between :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"517"},":ska":{"S":"2022-07-03"}, ":skb":{"S":"2022-07-05"}}'

echo 3 meetings by date and building/room/floor
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND gZ_b_SK between :ska AND :skb" \
    --filter-expression "contains(gZ_b_SK1, :skc)"\
    --expression-attribute-values '{":pk":{"S":"664"},":ska":{"S":"2022-07-03"}, ":skb":{"S":"2022-07-05"}, ":skc":{"S":"122.194"}}'

echo 4-8 get employee stuff by email
aws dynamodb query --table DemoEncrypted --index-name GSI-1 \
    --key-condition-expression "gZ_b_PK1 = :pk1 AND gZ_b_SK1 > :sk1" \
    --expression-attribute-values '{":pk1":{"S":"824"},":sk1":{"S":"2022-07-03"}}'

echo 9 employee by ID
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND begins_with(gZ_b_SK, :sk)" \
    --expression-attribute-values '{":pk":{"S":"517"},":sk":{"S":"E~"}}'

echo 10 employee by email
aws dynamodb query --table DemoEncrypted --index-name GSI-1 \
    --key-condition-expression "gZ_b_PK1 = :pk AND begins_with(gZ_b_SK1, :sk)" \
    --expression-attribute-values '{":pk":{"S":"889"},":sk":{"S":"E~"}}'

echo 11 Get ticket history by ticket ID
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk" \
    --expression-attribute-values '{":pk":{"S":"530"}}'

echo 12 Get ticket history by author
aws dynamodb query --table DemoEncrypted --index-name GSI-1 \
    --key-condition-expression "gZ_b_PK1 = :pk1" \
    --filter-expression "gZ_b_PK = :pk"\
    --expression-attribute-values '{":pk1":{"S":"884"},":pk":{"S":"530"}}'

echo 13 Get ticket history by asignee
aws dynamodb query --table DemoEncrypted --index-name GSI-2 \
    --key-condition-expression "gZ_b_PK2 = :pk2" \
    --filter-expression "gZ_b_PK = :pk"\
    --expression-attribute-values '{":pk2":{"S":"824"},":pk":{"S":"530"}}'

echo 14 employee by location
aws dynamodb query --table DemoEncrypted --index-name GSI-3 \
    --key-condition-expression "gZ_b_PK3 = :pk AND begins_with(gZ_b_SK3, :sk)" \
    --expression-attribute-values '{":pk":{"S":"822"},":sk":{"S":"~.40.122"}}'

echo 15 employee by manager
aws dynamodb query --table DemoEncrypted --index-name GSI-2 \
    --key-condition-expression "gZ_b_PK2 = :pk AND begins_with(gZ_b_SK, :sk)" \
    --expression-attribute-values '{":pk":{"S":"884"},":sk":{"S":"E~"}}'

echo 16 Get tickets by asignee
aws dynamodb query --table DemoEncrypted --index-name GSI-2 \
    --key-condition-expression "gZ_b_PK2 = :pk2" \
    --expression-attribute-values '{":pk2":{"S":"824"}}'

echo 17 Get tickets touched in last 24 hours
aws dynamodb query --table DemoEncrypted --index-name GSI-3 \
    --key-condition-expression "gZ_b_PK3 = :pk3 AND (gZ_b_SK3 > :sk3)" \
    --expression-attribute-values '{":pk3":{"S":"799"},":sk3":{"S":"2022-10-07T15"}}'

echo 18 project by status start and target date
aws dynamodb query --table DemoEncrypted --index-name GSI-1 \
    --key-condition-expression "gZ_b_PK1 = :pk1 AND (gZ_b_SK1 > :sk1)" \
    --filter-expression "Target < :target"\
    --expression-attribute-values '{":pk1":{"S":"309"},":sk1":{"S":"2022-01-01"},":target":{"S":"2023-03-03"}}'

echo 19 project by name
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND gZ_b_SK = :sk" \
    --expression-attribute-values '{":pk":{"S":"639"},":sk":{"S":"~.639."}}'

echo 20 get project history by date range
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND gZ_b_SK between :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"639"},":ska":{"S":"2022-09-11"},":skb":{"S":"2022-09-13"}}'

echo 21 get project history by role
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk" \
    --filter-expression "#role = :role"\
    --expression-attribute-names '{"#role":"gZ_b_Role"}' \
    --expression-attribute-values '{":pk":{"S":"639"},":role":{"S":"752"}}'


echo 22 get reservations by building ID
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk" \
    --expression-attribute-values '{":pk":{"S":"664"}}'

echo 23 get reservations by building ID and time range
aws dynamodb query --table DemoEncrypted --index-name GSI-0 \
    --key-condition-expression "gZ_b_PK = :pk AND gZ_b_SK BETWEEN :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"664"},":ska":{"S":"2022-07-03"},":skb":{"S":"2022-07-05"}}'

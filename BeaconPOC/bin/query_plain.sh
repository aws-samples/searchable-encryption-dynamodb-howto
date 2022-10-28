#!/bin/bash -eu

echo 1 meetings by date and email
aws dynamodb query --table DemoPlain --index-name GSI-1 \
    --key-condition-expression "PK1 = :pk1 AND SK1 between :sk1a AND :sk1b" \
    --expression-attribute-values '{":pk1":{"S":"able@gmail.com"},":sk1a":{"S":"2022-07-03"}, ":sk1b":{"S":"2022-07-05"}}'

echo 2 meetings by date and employee id
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND SK between :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"emp_001"},":ska":{"S":"2022-07-03"}, ":skb":{"S":"2022-07-05"}}'

echo 3 meetings by date and building/room/floor
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND SK between :ska AND :skb" \
    --filter-expression "contains(SK1, :skc)"\
    --expression-attribute-values '{":pk":{"S":"SEA33"},":ska":{"S":"2022-07-03"}, ":skb":{"S":"2022-07-05"}, ":skc":{"S":"12.403"}}'

echo 4-8 get employee stuff by email
aws dynamodb query --table DemoPlain --index-name GSI-1 \
    --key-condition-expression "PK1 = :pk1 AND SK1 > :sk1" \
    --expression-attribute-values '{":pk1":{"S":"able@gmail.com"},":sk1":{"S":"2022-07-03"}}'

echo 9 employee by ID
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND begins_with(SK, :sk)" \
    --expression-attribute-values '{":pk":{"S":"emp_001"},":sk":{"S":"E~"}}'

echo 10 employee by email
aws dynamodb query --table DemoPlain --index-name GSI-1 \
    --key-condition-expression "PK1 = :pk AND begins_with(SK1, :sk)" \
    --expression-attribute-values '{":pk":{"S":"barney@gmail.com"},":sk":{"S":"E~"}}'

echo 11 Get ticket history by ticket ID
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk" \
    --expression-attribute-values '{":pk":{"S":"ticket_001"}}'

echo 12 Get ticket history by author
aws dynamodb query --table DemoPlain --index-name GSI-1\
    --key-condition-expression "PK1 = :pk1" \
    --filter-expression "PK = :pk"\
    --expression-attribute-values '{":pk1":{"S":"zorro@gmail.com"},":pk":{"S":"ticket_001"}}'

echo 13 Get ticket history by asignee
aws dynamodb query --table DemoPlain --index-name GSI-2\
    --key-condition-expression "PK2 = :pk2" \
    --filter-expression "PK = :pk"\
    --expression-attribute-values '{":pk2":{"S":"able@gmail.com"},":pk":{"S":"ticket_001"}}'

echo 14 employee by location
aws dynamodb query --table DemoPlain --index-name GSI-3 \
    --key-condition-expression "PK3 = :pk AND begins_with(SK3, :sk)" \
    --expression-attribute-values '{":pk":{"S":"SEA"},":sk":{"S":"~44.12"}}'

echo 15 employee by manager
aws dynamodb query --table DemoPlain --index-name GSI-2 \
    --key-condition-expression "PK2 = :pk AND begins_with(SK, :sk)" \
    --expression-attribute-values '{":pk":{"S":"zorro@gmail.com"},":sk":{"S":"E~"}}'

echo 16 Get tickets by asignee
aws dynamodb query --table DemoPlain --index-name GSI-2\
    --key-condition-expression "PK2 = :pk2" \
    --expression-attribute-values '{":pk2":{"S":"able@gmail.com"}}'

echo 17 Get tickets touched in last 24 hours
aws dynamodb query --table DemoPlain --index-name GSI-3 \
    --key-condition-expression "PK3 = :pk3 AND (SK3 > :sk3)" \
    --expression-attribute-values '{":pk3":{"S":"3"},":sk3":{"S":"2022-10-07T15"}}'

echo 18 project by status start and target date
aws dynamodb query --table DemoPlain --index-name GSI-1 \
    --key-condition-expression "PK1 = :pk1 AND (SK1 > :sk1)" \
    --filter-expression "Target < :target"\
    --expression-attribute-values '{":pk1":{"S":"Active"},":sk1":{"S":"2022-01-01"},":target":{"S":"2023-03-03"}}'

echo 19 project by name
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND SK = :sk" \
    --expression-attribute-values '{":pk":{"S":"project_002"},":sk":{"S":"~project_002"}}'

echo 20 get project history by date range
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND SK between :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"project_002"},":ska":{"S":"2022-09-11"},":skb":{"S":"2022-09-13"}}'

echo 21 get project history by role
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk" \
    --filter-expression "#role = :role"\
    --expression-attribute-names '{"#role":"Role"}' \
    --expression-attribute-values '{":pk":{"S":"project_002"},":role":{"S":"PM"}}'

echo 22 get reservations by building ID
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk" \
    --expression-attribute-values '{":pk":{"S":"SEA33"}}'

echo 23 get reservations by building ID and time range
aws dynamodb query --table DemoPlain \
    --key-condition-expression "PK = :pk AND SK BETWEEN :ska AND :skb" \
    --expression-attribute-values '{":pk":{"S":"SEA33"},":ska":{"S":"2022-07-03"},":skb":{"S":"2022-07-05"}}'

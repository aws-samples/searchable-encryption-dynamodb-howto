#!/bin/bash -eu

aws dynamodb batch-write-item --request-items file://plain_json/employee.json
aws dynamodb batch-write-item --request-items file://plain_json/ticket.json
aws dynamodb batch-write-item --request-items file://plain_json/project.json
aws dynamodb batch-write-item --request-items file://plain_json/emeeting.json
aws dynamodb batch-write-item --request-items file://plain_json/timecard.json
aws dynamodb batch-write-item --request-items file://plain_json/reservation.json

#!/bin/bash -eu

aws dynamodb batch-write-item --request-items file://encrypted_json/employee.json
aws dynamodb batch-write-item --request-items file://encrypted_json/ticket.json
aws dynamodb batch-write-item --request-items file://encrypted_json/project.json
aws dynamodb batch-write-item --request-items file://encrypted_json/emeeting.json
aws dynamodb batch-write-item --request-items file://encrypted_json/timecard.json
aws dynamodb batch-write-item --request-items file://encrypted_json/reservation.json

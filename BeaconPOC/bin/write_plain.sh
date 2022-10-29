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

aws dynamodb batch-write-item $remote --request-items file://plain_json/employee.json
aws dynamodb batch-write-item $remote --request-items file://plain_json/ticket.json
aws dynamodb batch-write-item $remote --request-items file://plain_json/project.json
aws dynamodb batch-write-item $remote --request-items file://plain_json/emeeting.json
aws dynamodb batch-write-item $remote --request-items file://plain_json/timecard.json
aws dynamodb batch-write-item $remote --request-items file://plain_json/reservation.json

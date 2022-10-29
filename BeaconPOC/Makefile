default:
	@echo make build - builds the rust app to convert text to json
	@echo make text - runs the rust app, converting text to json
	@echo make create_databases - create the dynamodb tables and indexes on local DynamoDB
	@echo make create_databases_remote - create the dynamodb tables and indexes on remote DynamoDB
	@echo make import - writes the json files to local DynamoDB
	@echo make import_remote - writes the json files to remote DynamoDB
	@echo make check - check that all the queries return the same number of results in both modes
	@echo make check_remote - check that all the queries return the same number of results in both modes

build:
	cd rust && cargo build

text: build
	./rust/target/debug/demo_import

import:
	bin/write_plain.sh local
	bin/write_encrypted.sh local

import_remote:
	bin/write_plain.sh remote
	bin/write_encrypted.sh remote

check:
	bin/query_plain.sh local | fgrep '"Count"' > plain_counts
	bin/query_encrypted.sh local | fgrep '"Count"' > encrypted_counts
	diff plain_counts encrypted_counts

check_remote:
	bin/query_plain.sh remote | fgrep '"Count"' > plain_counts
	bin/query_encrypted.sh remote | fgrep '"Count"' > encrypted_counts
	diff plain_counts encrypted_counts

create_databases:
	bin/create_plain_db.sh local
	bin/create_encrypted_db.sh local

create_databases_remote:
	bin/create_plain_db.sh remote
	bin/create_encrypted_db.sh remote


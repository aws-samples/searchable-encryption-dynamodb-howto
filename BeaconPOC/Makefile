default:
	@echo make build - builds the rust app to convert text to json
	@echo make text - runs the rust app, converting text to json
	@echo make import - writes the json files to DynamoDB
	@echo make check - check all the queries return the same number of results in both modes
	@echo make create_databases - create the dynamodb tables and indexes

build:
	cd rust && cargo build

text: build
	./rust/target/debug/demo_import

import:
	bin/write_plain.sh
	bin/write_encrypted.sh

check:
	bin/query_plain.sh | fgrep '"Count"' > plain_counts
	bin/query_encrypted.sh | fgrep '"Count"' > encrypted_counts
	diff plain_counts encrypted_counts

create_databases:
	create_plain_db.sh
	create_encrypted_db.sh


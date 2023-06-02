# This will make all the targets
# in this makefile PHONY
.PHONY: $(MAKECMDGOALS)

zip: clean_workshop
	find "./workshop/java" -type d -name ".gradle" -prune -exec rm -rf {} \;
	cd workshop; zip -r ../assets/archive.zip .
	cd ..
	tar -czvf assets/testing.tar.gz utils content Makefile

put_assets:
	aws s3 sync ./assets s3://ws-assets-us-east-1/92b06038-2f5d-4a28-81e6-1ae85294cb42 --delete

get_assets:
	aws s3 sync s3://ws-assets-us-east-1/92b06038-2f5d-4a28-81e6-1ae85294cb42 ./assets --delete

# This command is strange to make it portable
# This way it can run in the Cloud9 env
clean_workshop:
	find "./workshop/java" -type d -name "build" -prune -exec rm -rf {} \;
	cd workshop; git restore --source=HEAD --staged --worktree -- "./java/"
	cd workshop; git restore --source=HEAD --staged --worktree -- config.toml

# These are in explicit order.
# This order mirrors the steps in the exercise.
# Since each test is run one-at-a-time
# this will replicate how customers will interact
# with the workshop
markdown_test: clean_workshop
	npx txm --jobs 1 ./content/exercise-1.en.md
	npx txm --jobs 1 ./content/exercise-2.en.md
	npx txm --jobs 1 ./content/exercise-3.en.md
	npx txm --jobs 1 ./content/exercise-4.en.md

# The expectation is that you run stop_ddb_local seprately
# This is because a test may fail,
# but also you may want to have access to the data
# to check the stat of ddb.
test_local: USE_DDB_LOCAL=true
test_local: start_ddb_local markdown_test

get_ddb_local:
	mkdir dynamodb_local
	curl -sSL "https://s3.us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.tar.gz" | tar -xzf - -C "dynamodb_local"

start_ddb_local:
	java -Djava.library.path=dynamodb_local/DynamoDBLocal_lib -jar dynamodb_local/DynamoDBLocal.jar -sharedDb -inMemory &

stop_ddb_local:
	@pkill -f DynamoDBLocal.jar

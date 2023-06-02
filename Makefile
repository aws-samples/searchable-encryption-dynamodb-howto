 
zip: clean_exercises
	find "./exercises/java" -type d -name ".gradle" -prune -exec rm -rf {} \;
	cd exercises; zip -r ../assets/archive.zip .

put_assets:
	aws s3 sync ./assets s3://ws-assets-us-east-1/92b06038-2f5d-4a28-81e6-1ae85294cb42 --delete

get_assets:
	aws s3 sync s3://ws-assets-us-east-1/92b06038-2f5d-4a28-81e6-1ae85294cb42 ./assets --delete

clean_exercises:
	find "./exercises/java" -type d -name "build" -prune -exec rm -rf {} \;
	git restore --source=HEAD --staged --worktree -- "./exercises/java/"
	git restore --source=HEAD --staged --worktree -- exercises/config.toml

test_markdown:
	# find content -name '*.md' | xargs -t -I %  npx txm %
	npx txm --jobs 1 ./content/exercise-1.en.md

test: clean_exercises | test_markdown 

get-ddb-local:
	mkdir dynamodb_local
	curl -sSL "https://s3.us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.tar.gz" | tar -xzf - -C "dynamodb_local"

start-ddb-local:
	java -Djava.library.path=dynamodb_local/DynamoDBLocal_lib -jar dynamodb_local/DynamoDBLocal.jar -sharedDb -inMemory &

stop-ddb-local:
	@pkill -f DynamoDBLocal.jar

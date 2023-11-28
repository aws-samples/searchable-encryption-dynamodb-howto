#!/bin/bash

#
# Copyright 2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this
# software and associated documentation files (the "Software"), to deal in the Software
# without restriction, including without limitation the rights to use, copy, modify,
# merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
# INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
# PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
# HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
# OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

#title           own_account.sh
#date            2023-04-27
#version         1.0
#usage           sh own_account.sh
#==============================================================================

echo 'Starting Workshop CloudFormation stack'
aws cloudformation create-stack \
  --stack-name Workshop \
  --template-body file://workshop.yaml \
  --capabilities CAPABILITY_NAMED_IAM \
  --parameters \
      ParameterKey=RepoName,ParameterValue=Workshop \
      ParameterKey=S3CodeBucket,ParameterValue='' \
      ParameterKey=S3CodeKey,ParameterValue='' 

echo 'Initializing git'
git config --global user.name "Workshop User"
git config --global user.email workshop.user@amazon.com
git config --global credential.helper '!aws codecommit credential-helper $@'
git config --global credential.UseHttpPath true

echo 'Waiting for Workshop CloudFormation stack to complete'
aws cloudformation wait stack-create-complete --stack-name Workshop
echo 'Workshop stack complete'

echo 'Cloning Repository'
git clone https://git-codecommit.$AWS_REGION.amazonaws.com/v1/repos/Workshop

echo 'Extracting archive code from zip file, overwriting any existing files'
unzip -o archive.zip -d Workshop

echo 'Uploading code to repository'
cd Workshop
git checkout -b main
git add .
git commit -m "Initial Commit"
git push --set-upstream origin main
cd ..

# Wait to make sure that the SSM Document exists and starts running
sleep 10

echo -e '\n\nCloud9 instance is created\n\n'

echo Wait for the following command Workshop-Cloud9BootStrapSSMDocument-*
echo to run successfully.
echo this will bootstrap the Cloud9 instance with the required dependencies
echo aws ssm list-commands --query 'Commands[*].[CommandId,Status,DocumentName]' --output table
aws ssm list-commands --query 'Commands[*].[CommandId,Status,DocumentName]' --output table

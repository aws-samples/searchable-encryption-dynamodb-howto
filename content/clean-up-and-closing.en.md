---
# TODO Cleaning Up
title : "Thank You!"
weight : 1000
---

# Thank You!

Thank you for working through the workshop today. We hope that you found it to be useful in getting started with client-side encryption 
and searchable encryption using the AWS Database Encryption SDK and AWS KMS.

# Cleaning Up

## AWS Classroom

If you are working through these exercises in an AWS classroom environment, your AWS resources will be automatically torn down. You do not need to take any action to clean up your resources.

## Personal AWS account

If you are using your own AWS account for these workshops, you will need to clean up your resources. 

Some resources were deploying using CloudFormation. To delete these resources, delete the stack you deployed through the CloudFormation console.

The DynamoDB resources were not created through CFN. You will need to clean these up manually. To do this, run  

```bash
cd ~/environment/workshop
java/plaintext-base/employee-portal delete-table
java/exercise-1/employee-portal delete-table
java/exercise-2/employee-portal delete-table
java/exercise-3/employee-portal delete-table
java/exercise-4/employee-portal delete-table
aws dynamodb delete-table --table-name BranchKey_Table
```

That's it! Your workshop resources have been torn down.

# Feedback

We welcome comments, questions, concerns, contributions, and feature requests [on our GitHub page for the Searchable Encryption DynamoDB Workshop](https://github.com/aws-samples/searchable-encryption-dynamodb-howto).

If there is content that can be improved or anything that you would like to see, we would like to cover it for you.

At AWS Cryptography, our mission is to make tools that are easy to use, hard to misuse, and that help our customers protect their most sensitive data wherever and whenever it is.

We look forward to hearing from you about this workshop or your needs.

Thank you again for your time, and go forth and be secure!

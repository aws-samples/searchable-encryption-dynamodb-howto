---
title: Using Your Own AWS Account
weight: 20
---

:::alert{header=Note}
This section only applies if you are running the workshop in your own account.
:::

## Workshop Resources

## Download and execute the bootstrap script

We will download the zip file containing the workshop's template code, the CloudFormation templates used to provision the resources, and the bootstrap script. We will then run the bootstrap script to provision the resources.

1. Sign in to the AWS Management Console and open the AWS CloudShell console at https://console.aws.amazon.com/cloudshell/ .

1. In the menu bar, for Select a Region, choose the region in which you will be running the workshop.

Select Region

3. Run the following code in CloudShell terminal.

To download the code associated with the workshop:

:::code{showCopyAction=true showLineNumbers=false language=bash}

# Download code zipfile
curl -sSL ':assetUrl{path="/archive.zip" source=s3}'
# Download CloudFormation template
curl -sSL ':assetUrl{path="/static/cfn/workshop.yaml" source=repo}'
# Download bootstrap script
curl -sSL ':assetUrl{path="/static/own_account.sh" source=repo}'

:::

4. On the **Safe Paste** dialog, choose **Paste**.

5. Run the bootstrap script in the CloudShell terminal.

:::code{showCopyAction=true showLineNumbers=false language=bash}

sh own_account.sh

:::

:::alert{header=Note}
The Cloud9 deployment and setup process takes approximately 5 minutes to complete.
:::


:::alert{header="Important" type="warning"}
If you are running this workshop on your own AWS account, remember to delete all resources by following the [Clean Up Resources](../../clean-up-and-closing.en.md) section to avoid unnecessary charges.
:::


# Start the workshop!

Now that you have all of the prerequisite resources set up
and understand how to interact with them,
you can now start the first exercise:
[Adding the Database Encryption SDK](../../exercise-1.en.md).

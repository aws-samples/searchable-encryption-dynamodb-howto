---
title : "Tips and Troubleshooting"
weight : 10000
---

# Tips and Troubleshooting

This page contains reference information that might be useful as you work through the workshop.

## Troubleshooting

### Missing CloudFormation resources

Make sure you take the defaults for which region to launch in (us-east-2). If you've changed the region for any part of the workshop, tear your stack down and start fresh.

### More disk space on Cloud9

Working through the workshop, you might find that you are out of disk. If this happens, use the following script to expand your EBS volume. (<a href="https://docs.aws.amazon.com/cloud9/latest/user-guide/move-environment.html#move-environment-resize" target="_blank">Script source</a>)

Note that this is adapted from the script source because the Busy Engineer's Cloud9 instances run on EC2 Nitro, which has different block device identifiers.

Step by step:

1. Save the script below to `resize.sh`
1. `chmod +x resize.sh`
1. `./resize.sh`
  * By default, this will increase your volume to 20GB, but you may supply a different number if you prefer.

```bash
#!/bin/bash

# Specify the desired volume size in GiB as a command-line argument. If not specified, default to 20 GiB.
SIZE=${1:-20}

# Install the jq command-line JSON processor.
sudo yum -y install jq

# Get the ID of the envrionment host Amazon EC2 instance.
INSTANCEID=$(curl http://169.254.169.254/latest/meta-data//instance-id)

# Get the ID of the Amazon EBS volume associated with the instance.
VOLUMEID=$(aws ec2 describe-instances --instance-id $INSTANCEID | jq -r .Reservations[0].Instances[0].BlockDeviceMappings[0].Ebs.VolumeId)

# Resize the EBS volume.
aws ec2 modify-volume --volume-id $VOLUMEID --size $SIZE

# Wait for the resize to finish.
while [ "$(aws ec2 describe-volumes-modifications --volume-id $VOLUMEID --filters Name=modification-state,Values="optimizing","completed" | jq '.VolumesModifications | length')" != "1" ]; do
  sleep 1
  done

# Rewrite the partition table so that the partition takes up all the space that it can.
sudo growpart /dev/nvme0n1p1 1

# Expand the size of the file system.
sudo resize2fs /dev/nvme0n1p1
```

## Tips

### API Documentation

Python and Java have API documentation available for each exercise. You can view the documentation as you work in Cloud9.

::::tabs{variant="container" groupId=codeSample}
:::tab{label="Java"}

```bash 
make javadoc
```

:::
::::

Now select "Preview -> Preview Running Application" from the Cloud9 menu bar.

Cloud9 will open a new pane in your IDE with a web browser rendering your API documentation.

### Cloud9

Cloud9 has lots of IDE features for you to leverage. Here's some links to help you make the most of your Cloud9 experience. (Links all open in a new window.)

* <a href="https://docs.aws.amazon.com/cloud9/latest/user-guide/menu-commands.html" target="_blank">Cloud9 Code Navigation</a>
* <a href="https://docs.aws.amazon.com/cloud9/latest/user-guide/settings-keybindings.html" target="_blank">Cloud9 Keybindings</a>
* <a href="https://docs.aws.amazon.com/cloud9/latest/user-guide/tutorial.html" target="_blank">Cloud9 Tutorial</a>

### Cryptographic Details

The Busy Engineer's Document Bucket only scratches the surface of the features offered by AWS KMS. To dive deep on how KMS can be useful to your application, check out the <a href="https://docs.aws.amazon.com/kms/latest/cryptographic-details/intro.html" target="_blank">AWS Key Management Service Cryptographic Details Docs</a>, for more information on the details of encryption, decryption, random number generation procedures, and more within KMS.



:::code{showCopyAction=true showLineNumbers=false language=bash}

sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

curl ':assetUrl{path="/AwsCryptographicMaterialProviders-1.0-SNAPSHOT.jar" source=s3}' -o AwsCryptographicMaterialProviders-1.0-SNAPSHOT.jar
curl ':assetUrl{path="/AwsCryptographicMaterialProviders-1.0-SNAPSHOT.pom" source=s3}' -o AwsCryptographicMaterialProviders-1.0-SNAPSHOT.pom

curl ':assetUrl{path="/aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.pom" source=s3}' -o aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.jar
curl ':assetUrl{path="/aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.jar" source=s3}' -o aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.pom

mvn -B -ntp install:install-file \
  -Dfile=AwsCryptographicMaterialProviders-1.0-SNAPSHOT.jar \
  -DpomFile=AwsCryptographicMaterialProviders-1.0-SNAPSHOT.pom;
mvn -B -ntp install:install-file \
  -Dfile=aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.jar \
  -DpomFile=aws-database-encryption-sdk-dynamodb-1.0-SNAPSHOT.pom;

:::
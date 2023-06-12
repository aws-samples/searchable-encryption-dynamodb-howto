---
title : "Tips and Troubleshooting"
weight : 10000
---

This page contains reference information that might be useful as you work through the workshop.

## Troubleshooting

### Missing CloudFormation resources

Make sure you take the defaults for which region to launch in (us-west-2).
If you've changed the region for any part of the workshop, tear your stack down and start fresh.

### More disk space on Cloud9

Working through the workshop, you might find that you are out of disk.
The workshop resizes the disk from it's default to 32 GB
so hopefully you won't have any trouble.
If this happens, use the following script to expand your EBS volume.
(<a href="https://docs.aws.amazon.com/cloud9/latest/user-guide/move-environment.html#move-environment-resize" target="_blank">Script source</a>)

Note that this is adapted from the script source because the Busy Engineer's Cloud9 instances run on EC2 Nitro,
which has different block device identifiers.

Step by step:

1. Save the script below to `resize.sh`
1. `chmod +x resize.sh`
1. `./resize.sh`
  * By default, this will increase your volume to 64GB, but you may supply a different number if you prefer.

```bash
#!/bin/bash

# Specify the desired volume size in GiB as a command-line argument. If not specified, default to 64 GiB.
SIZE=${1:-64}

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

### Cloud9

Cloud9 has lots of IDE features for you to leverage. Here's some links to help you make the most of your Cloud9 experience. (Links all open in a new window.)

* [Cloud9 Code Navigation](https://docs.aws.amazon.com/cloud9/latest/user-guide/menu-commands.html)
* [Cloud9 Keybindings](https://docs.aws.amazon.com/cloud9/latest/user-guide/settings-keybindings.html)
* [Cloud9 Tutorial](https://docs.aws.amazon.com/cloud9/latest/user-guide/tutorial.html)

### Cryptographic Details

The Busy Engineer's Database Encryption only scratches the surface of the features offered by AWS KMS.
To dive deep on how KMS can be useful to your application,
check out the [AWS Key Management Service Cryptographic Details Docs](https://docs.aws.amazon.com/kms/latest/cryptographic-details/intro.html),
for more information on the details of encryption, decryption, random number generation procedures, and more within KMS.

### Testing / Automating the workshop

This is a helper link for testing.
It could also be used to complete all the steps automatically,
but where is the fun in that?

This `curl` command will download the required assets
into your Cloud9 Environment.

:::code{showCopyAction=true showLineNumbers=false language=bash}

curl -sSL ':assetUrl{path="/testing.tar.gz" source=s3}' | tar -xzf - -C ~/environment

:::

Now, in the `~/environment` directory run the following command.
This will install and run `txm` from the `npm` repository
and run all the tests that exist in the content markdown.
This will erase all work you have done on the workshop!

:::code{showCopyAction=true showLineNumbers=false language=bash}

make markdown_test

:::

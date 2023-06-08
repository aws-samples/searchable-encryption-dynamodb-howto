// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class AwsSupport {

  private AwsSupport() { // Do not instantiate
  }

  public static DynamoDbClientBuilder GetClientBuilder(boolean ddbLocal)
  {
    if (ddbLocal)
      return DynamoDbClient.builder()
      .endpointOverride(URI.create("http://localhost:8000"));
    else
      return DynamoDbClient.builder();
  }

  public static DynamoDbClient MakeDynamoDbClient(SharedOptions shared)
  {
    if (shared.plain)
      return GetClientBuilder(shared.ddbLocal)
              .build();
    else
      return GetClientBuilder(shared.ddbLocal)
              .build();
  }

  public static String CreateBranchKey(boolean ddbLocal) {
    return "No branch keys in plain text";
  }

}

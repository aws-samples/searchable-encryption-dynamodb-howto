// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import com.moandjiezana.toml.Toml;
import java.io.File;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import java.net.URI;
import java.util.HashMap;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class AwsSupport {

  private AwsSupport() { // Do not instantiate
  }

  public static DynamoDbClientBuilder GetClientBuilder()
  {
    if (USE_LOCAL_DDB)
      return DynamoDbClient.builder()
      .endpointOverride(URI.create("http://localhost:8000"));
    else
      return DynamoDbClient.builder();
  }

  public static DynamoDbClient MakeDynamoDbClient()
  {
    return GetClientBuilder()
      .build();
  }

  public static DynamoDbClient MakeDynamoDbClientPlain()
  {
    return GetClientBuilder().build();
  }

  public static String CreateBranchKey() {
    return "No branch keys in plain text";
  }

}

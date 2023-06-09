// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

public class AwsSupport {

  private static boolean ddbLocal; // Internal testing flag if using a local DDB instance

  private AwsSupport() { // Do not instantiate
  }

  public static void setDdbLocal(boolean _ddbLocal) {
    ddbLocal = _ddbLocal;
  }

  public static DynamoDbClientBuilder GetClientBuilder()
  {
    if (ddbLocal)
      return DynamoDbClient.builder()
      .endpointOverride(URI.create("http://localhost:8000"));
    else
      return DynamoDbClient.builder();
  }

  public static DynamoDbClient MakeDynamoDbClient(SharedOptions shared)
  {
    ddbLocal = shared.ddbLocal;

    if (shared.plain)
      return GetClientBuilder()
              .build();
    else
      return GetClientBuilder()
              .build();
  }

  public static String CreateBranchKey() {
    return "No branch keys in plain text";
  }

}

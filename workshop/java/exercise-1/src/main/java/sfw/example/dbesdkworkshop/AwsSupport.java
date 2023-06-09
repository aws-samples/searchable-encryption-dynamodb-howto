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

// BEGIN EXERCISE 1 STEP 3b

// END EXERCISE 1 STEP 3b

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
        // BEGIN EXERCISE 1 STEP 5a

        // END EXERCISE 1 STEP 5a
        .build();
  }

  // BEGIN EXERCISE 1 STEP 3c

  // END EXERCISE 1 STEP 3c

  // BEGIN EXERCISE 1 STEP 4b

  // END EXERCISE 1 STEP 4b

  // BEGIN EXERCISE 1 STEP 5b

  // END EXERCISE 1 STEP 5b
}

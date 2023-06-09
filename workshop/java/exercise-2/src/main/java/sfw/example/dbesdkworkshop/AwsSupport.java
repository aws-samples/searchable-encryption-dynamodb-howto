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

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.*;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.*;
import software.amazon.cryptography.dbencryptionsdk.structuredencryption.model.CryptoAction;
import software.amazon.cryptography.keystore.KeyStore;
import software.amazon.cryptography.keystore.model.CreateKeyOutput;
import software.amazon.cryptography.keystore.model.CreateKeyStoreInput;
import software.amazon.cryptography.keystore.model.KMSConfiguration;
import software.amazon.cryptography.keystore.model.KeyStoreConfig;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.MaterialProviders;
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsHierarchicalKeyringInput;
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig;

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
        .overrideConfiguration(
          ClientOverrideConfiguration.builder()
            .addExecutionInterceptor(MakeInterceptor())
            .build())
        .build();
  }

  public static KeyStore MakeKeyStore()
  {
    return KeyStore.builder().KeyStoreConfig(
      KeyStoreConfig.builder()
        .ddbClient(GetClientBuilder().build())
        .ddbTableName(BRANCH_KEY_TABLE)
        .logicalKeyStoreName(BRANCH_KEY_TABLE)
        .kmsClient(KmsClient.create())
        .kmsConfiguration(KMSConfiguration.builder()
          .kmsKeyArn(BRANCH_KEY_KMS_ARN)
          .build())
        .build()).build();
  }

  public static boolean IsTableReady(DescribeTableResponse resp)
  {
    if (resp.table().tableStatus() != TableStatus.ACTIVE) return false;
    for (GlobalSecondaryIndexDescription x : resp.table().globalSecondaryIndexes()) {
      if (x.indexStatus() != IndexStatus.ACTIVE) return false;
    }
    return true;
  }

  public static void WaitForTableReady(String tableName)
  {
    final DynamoDbClient ddbClient = GetClientBuilder().build();
    final DescribeTableRequest request =
      DescribeTableRequest.builder().tableName(tableName).build();
    while (true) {
      DescribeTableResponse resp = ddbClient.describeTable(request);
      if (IsTableReady(resp)) break;
      System.err.println("Waiting for table " + tableName + " to be ready...");
      try {Thread.sleep(500);} catch (Exception e) {}
    }
  }

  public static String CreateBranchKey() {
    final KeyStore keystore = MakeKeyStore();
    keystore.CreateKeyStore(CreateKeyStoreInput.builder().build());
    WaitForTableReady(BRANCH_KEY_TABLE);
    return keystore.CreateKey().branchKeyIdentifier();
  }

  // BEGIN EXERCISE 2 STEP 2
  
  // END EXERCISE 2 STEP 2

  // BEGIN EXERCISE 2 STEP 3a

  // END EXERCISE 2 STEP 3a
  
  // BEGIN EXERCISE 2 STEP 3b

  // END EXERCISE 2 STEP 3b

  // BEGIN EXERCISE 2 STEP 4a

  // END EXERCISE 2 STEP 4a
  
  // BEGIN EXERCISE 2 STEP 4b

  // END EXERCISE 2 STEP 4b
  
  // BEGIN EXERCISE 2 STEP 5a

  // END EXERCISE 2 STEP 5a

 public static IKeyring MakeHierarchicalKeyring()
  {
    final MaterialProviders matProv = MaterialProviders.builder()
      .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
      .build();

    final CreateAwsKmsHierarchicalKeyringInput keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
      .branchKeyId(BRANCH_KEY_ID)
      .keyStore(MakeKeyStore())
      .ttlSeconds(6000l)
      .maxCacheSize(100)
      .build();

    return matProv.CreateAwsKmsHierarchicalKeyring(keyringInput);
  }

  public static DynamoDbEncryptionInterceptor MakeInterceptor()
  {
    final IKeyring kmsKeyring = MakeHierarchicalKeyring();

    HashMap<String, CryptoAction> actions = new HashMap<String, CryptoAction>();
    actions.put(PARTITION_KEY, CryptoAction.SIGN_ONLY);
    actions.put(SORT_KEY, CryptoAction.SIGN_ONLY);

    actions.put(ASSIGNEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ATTENDEES_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(AUTHOR_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(DESCRIPTION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(DURATION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(EMPLOYEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(EMPLOYEE_NAME_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(HOURS_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(LOCATION_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(MANAGER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(MESSAGE_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ORGANIZER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(ROLE_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(SEVERITY_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(STATUS_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(SUBJECT_NAME, CryptoAction.ENCRYPT_AND_SIGN);
    actions.put(TITLE_NAME, CryptoAction.ENCRYPT_AND_SIGN);

    // These attributes cannot be encrypted, as they are used in the primary key
    // or are needed in ranged searches
    actions.put(EMPLOYEE_NUMBER_NAME, CryptoAction.SIGN_ONLY);
    actions.put(MODIFIED_DATE_NAME, CryptoAction.SIGN_ONLY);
    actions.put(PROJECT_NAME_NAME, CryptoAction.SIGN_ONLY);
    actions.put(RESERVATION_NAME, CryptoAction.SIGN_ONLY);
    actions.put(START_TIME_NAME, CryptoAction.SIGN_ONLY);
    actions.put(TARGET_DATE_NAME, CryptoAction.SIGN_ONLY);
    actions.put(TICKET_NUMBER_NAME, CryptoAction.SIGN_ONLY);

    DynamoDbTableEncryptionConfig tableConfig = DynamoDbTableEncryptionConfig.builder()
        .logicalTableName(TABLE_NAME)
        .partitionKeyName(PARTITION_KEY)
        .sortKeyName(SORT_KEY)
        .attributeActionsOnEncrypt(actions)
        .keyring(kmsKeyring)
        // BEGIN EXERCISE 2 STEP 5b

        // END EXERCISE 2 STEP 5b
        .build();

    HashMap<String, DynamoDbTableEncryptionConfig> tables = new HashMap<String, DynamoDbTableEncryptionConfig>();
    tables.put(TABLE_NAME, tableConfig);
    DynamoDbTablesEncryptionConfig config = DynamoDbTablesEncryptionConfig.builder()
        .tableEncryptionConfigs(tables)
        .build();

    return DynamoDbEncryptionInterceptor.builder().config(config).build();
  }
}

// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.kms.KmsClient;

import software.amazon.cryptography.dbencryptionsdk.dynamodb.DynamoDbEncryptionInterceptor;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.BeaconKeySource;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.BeaconVersion;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.CompoundBeacon;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.Constructor;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.ConstructorPart;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.DynamoDbTableEncryptionConfig;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.DynamoDbTablesEncryptionConfig;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.EncryptedPart;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.SearchConfig;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.SingleKeyStore;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.StandardBeacon;

import software.amazon.cryptography.dbencryptionsdk.structuredencryption.model.CryptoAction;

import software.amazon.cryptography.keystore.KeyStore;
import software.amazon.cryptography.keystore.model.CreateKeyInput;
import software.amazon.cryptography.keystore.model.CreateKeyOutput;
import software.amazon.cryptography.keystore.model.CreateKeyStoreInput;
import software.amazon.cryptography.keystore.model.KMSConfiguration;
import software.amazon.cryptography.keystore.model.KeyStoreConfig;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.MaterialProviders;
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsHierarchicalKeyringInput;
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig;

import static sfw.example.dbesdkworkshop.Config.Constants.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

public class AwsSupport {

  private static boolean ddbLocal; // Internal testing flag if using a local DDB instance

  private AwsSupport() { // Do not instantiate
  }

  public static void setDdbLocal(boolean _ddbLocal) {
    ddbLocal = _ddbLocal;
  }

  public static DynamoDbClientBuilder GetClientBuilder() {
    if (ddbLocal)
      return DynamoDbClient.builder()
          .endpointOverride(URI.create("http://localhost:8000"));
    else
      return DynamoDbClient.builder();
  }

  public static DynamoDbClient MakeDynamoDbClient(SharedOptions shared) {
    ddbLocal = shared.ddbLocal;

    if (shared.plain) {
      return GetClientBuilder()
          .build();
    } else {
      MaterialProviders mat = MaterialProviders
          .builder()
          .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
          .build();

      KeyStore keyStore = MakeKeystore();
      CreateAwsKmsHierarchicalKeyringInput input = CreateAwsKmsHierarchicalKeyringInput
          .builder()
          .keyStore(keyStore)
          .branchKeyId("8f921d9e-4fb6-4d6d-a735-efaf70c91035")
          .ttlSeconds(6000)
          .build();
      IKeyring keyring = mat.CreateAwsKmsHierarchicalKeyring(input);

      Map<String, CryptoAction> actions = Map.ofEntries(
          entry(PARTITION_KEY, CryptoAction.SIGN_ONLY),
          entry(SORT_KEY, CryptoAction.SIGN_ONLY),
          // entry(GSI1_PARTITION_KEY, CryptoAction.SIGN_ONLY),
          entry(GSI1_SORT_KEY, CryptoAction.SIGN_ONLY),
          entry(GSI2_PARTITION_KEY, CryptoAction.SIGN_ONLY),
          entry(GSI3_PARTITION_KEY, CryptoAction.SIGN_ONLY),
          entry(GSI3_SORT_KEY, CryptoAction.SIGN_ONLY),

          entry(EMPLOYEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(STATUS_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(ORGANIZER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(AUTHOR_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(MANAGER_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(ASSIGNEE_EMAIL_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(CITY_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(SEVERITY_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(ROLE_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(LOCATION_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(EMPLOYEE_NAME_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(TITLE_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(DURATION_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(ATTENDEES_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(SUBJECT_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(MESSAGE_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(DESCRIPTION_NAME, CryptoAction.ENCRYPT_AND_SIGN),
          entry(HOURS_NAME, CryptoAction.ENCRYPT_AND_SIGN),

          entry(EMPLOYEE_NUMBER_NAME, CryptoAction.SIGN_ONLY),
          entry(MODIFIED_DATE_NAME, CryptoAction.SIGN_ONLY),
          entry(PROJECT_NAME_NAME, CryptoAction.SIGN_ONLY),
          entry(RESERVATION_NAME, CryptoAction.SIGN_ONLY),
          entry(START_TIME_NAME, CryptoAction.SIGN_ONLY),
          entry(TARGET_DATE_NAME, CryptoAction.SIGN_ONLY),
          entry(TICKET_NUMBER_NAME, CryptoAction.SIGN_ONLY));

      SearchConfig search = SearchConfig
          .builder()
          .writeVersion(1)
          .versions(Arrays.asList(
              BeaconVersion
                  .builder()
                  .version(1)
                  .keyStore(keyStore)
                  .keySource(BeaconKeySource
                      .builder()
                      .single(SingleKeyStore
                          .builder()
                          .cacheTTL(6000)
                          .keyId("8f921d9e-4fb6-4d6d-a735-efaf70c91035")
                          .build())
                      .build())
                  .standardBeacons(Arrays.asList(
                      StandardBeacon.builder().name(EMPLOYEE_EMAIL_NAME).length(8).build(),
                      StandardBeacon.builder().name(STATUS_NAME).length(8).build(),
                      StandardBeacon.builder().name(ORGANIZER_EMAIL_NAME).length(8).build(),
                      StandardBeacon.builder().name(AUTHOR_EMAIL_NAME).length(8).build()))
                  .compoundBeacons(Arrays.asList(
                      CompoundBeacon
                          .builder()
                          .name(GSI1_PARTITION_KEY)
                          .encrypted(Arrays.asList(
                              EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build(),
                              EncryptedPart.builder().name(STATUS_NAME).prefix(STATUS_PREFIX).build(),
                              EncryptedPart.builder().name(ORGANIZER_EMAIL_NAME).prefix(ORGANIZER_EMAIL_PREFIX).build(),
                              EncryptedPart.builder().name(AUTHOR_EMAIL_NAME).prefix(AUTHOR_EMAIL_PREFIX).build()))
                          .constructors(Arrays.asList(
                              MakeConstructor(EMPLOYEE_EMAIL_NAME),
                              MakeConstructor(STATUS_NAME),
                              MakeConstructor(ORGANIZER_EMAIL_NAME),
                              MakeConstructor(AUTHOR_EMAIL_NAME)))
                          .split(SPLIT)
                          .build()))
                  .build()))
          .build();


      DynamoDbTableEncryptionConfig table = DynamoDbTableEncryptionConfig
          .builder()
          .logicalTableName(TABLE_NAME)
          .keyring(keyring)
          .attributeActionsOnEncrypt(actions)
          .search(search)
          .partitionKeyName(PARTITION_KEY)
          .sortKeyName(SORT_KEY)
          .build();
      Map<String, DynamoDbTableEncryptionConfig> tables = Map.of(
          TABLE_NAME, table);
      DynamoDbTablesEncryptionConfig tableConfig = DynamoDbTablesEncryptionConfig
          .builder()
          .tableEncryptionConfigs(tables)
          .build();
      DynamoDbEncryptionInterceptor interceptor = DynamoDbEncryptionInterceptor
          .builder()
          .config(tableConfig)
          .build();
      ClientOverrideConfiguration override = ClientOverrideConfiguration
          .builder()
          .addExecutionInterceptor(interceptor)
          .build();
      return GetClientBuilder()
          .overrideConfiguration(override)

          .build();
    }
  }

  private static Constructor MakeConstructor(String name) {
    return Constructor
        .builder()
        .parts(Arrays.asList(
            ConstructorPart.builder().name(name).required(true).build()))
        .build();
  }

  private static KeyStore MakeKeystore() {
    KeyStoreConfig config = KeyStoreConfig
        .builder()
        .ddbClient(GetClientBuilder().build())
        .ddbTableName("BranchKeyTable")
        .logicalKeyStoreName("BranchKeyTable")
        .kmsClient(KmsClient.create())
        .kmsConfiguration(KMSConfiguration.builder()
            .kmsKeyArn("arn:aws:kms:us-west-2:374405474937:key/mrk-1536c7bf2e9a418a894b5d3cf83ee897").build())
        .build();
    return KeyStore
        .builder()
        .KeyStoreConfig(config)
        .build();
  }

  public static String CreateBranchKey() {

    KeyStore keyStore = MakeKeystore();

    keyStore.CreateKeyStore(CreateKeyStoreInput.builder().build());

    CreateKeyOutput output = keyStore.CreateKey(CreateKeyInput
        .builder()
        .encryptionContext(Map.of("department", "admin"))
        .build());

    return output.branchKeyIdentifier();
  }

  // 8f921d9e-4fb6-4d6d-a735-efaf70c91035

}
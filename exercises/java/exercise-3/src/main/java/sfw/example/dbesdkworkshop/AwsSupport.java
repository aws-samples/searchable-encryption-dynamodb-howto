// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
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
        .overrideConfiguration(
          ClientOverrideConfiguration.builder()
            .addExecutionInterceptor(MakeInterceptor(shared.ddbLocal))
            .build())
        .build();
  }

  public static KeyStore MakeKeyStore(boolean ddbLocal)
  {
    return KeyStore.builder().KeyStoreConfig(
      KeyStoreConfig.builder()
        .ddbClient(GetClientBuilder(ddbLocal).build())
        .ddbTableName(BRANCH_KEY_TABLE)
        .logicalKeyStoreName(BRANCH_KEY_TABLE)
        .kmsClient(KmsClient.create())
        .kmsConfiguration(KMSConfiguration.builder()
          .kmsKeyArn(BRANCH_KEY_KMS_ARN)
          .build())
        .build()).build();
  }

  public static String CreateBranchKey(boolean ddbLocal) {
    final KeyStore keystore = MakeKeyStore(ddbLocal);    
    keystore.CreateKeyStore(CreateKeyStoreInput.builder().build());
    return keystore.CreateKey().branchKeyIdentifier();
  }

  public static BeaconKeySource MakeKeySource() {
    return BeaconKeySource.builder()
        .single(
            SingleKeyStore.builder()
                .keyId(BRANCH_KEY_ID)
                .cacheTTL(3600)
                .build())
        .build();
  }

  public static ArrayList<StandardBeacon> MakeStandardBeacons() {
    ArrayList<StandardBeacon> beacons = new ArrayList<StandardBeacon>();
    beacons.add(StandardBeacon.builder()
        .name(EMPLOYEE_EMAIL_NAME)
        .length(8)
        .build());
    // BEGIN EXERCISE 3 STEP 2

    // END EXERCISE 3 STEP 2

    return beacons;
  }

  public static Constructor MakeGsi1TimecardPartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(EMPLOYEE_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi1TimecardSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(START_TIME_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  // BEGIN EXERCISE 3 STEP 4

  // END EXERCISE 3 STEP 4

  public static CompoundBeacon MakeGsi1PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build());

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardPartitionKeyConstructor());
    // Gsi1EmployeePartitionKey is the same as Gsi1TimecardPartitionKey

    return CompoundBeacon.builder()
        .name(GSI1_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi1SortKey() {
    ArrayList<SignedPart> signedParts = new ArrayList<SignedPart>();
    signedParts.add(SignedPart.builder().name(START_TIME_NAME).prefix(START_TIME_PREFIX).build());
    // BEGIN EXERCISE 3 STEP 3

    // END EXERCISE 3 STEP 3

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardSortKeyConstructor());
    // BEGIN EXERCISE 3 STEP 5

    // END EXERCISE 3 STEP 5

    return CompoundBeacon.builder()
        .name(GSI1_SORT_KEY)
        .split(SPLIT)
        .signed(signedParts)
        .constructors(constructors)
        .build();
  }

  public static ArrayList<CompoundBeacon> MakeCompoundBeacons() {
    ArrayList<CompoundBeacon> beacons = new ArrayList<CompoundBeacon>();
    beacons.add(MakeGsi1PartitionKey());
    beacons.add(MakeGsi1SortKey());
    // BEGIN EXERCISE 3 STEP 6

    // END EXERCISE 3 STEP 6
    return beacons;
  }

  public static BeaconVersion MakeBeaconVersion(boolean ddbLocal) {
    return BeaconVersion.builder()
        .version(1)
        .keyStore(MakeKeyStore(ddbLocal))
        .keySource(MakeKeySource())
        .standardBeacons(MakeStandardBeacons())
        .compoundBeacons(MakeCompoundBeacons())
        .build();
  }

  public static SearchConfig MakeSearchConfig(boolean ddbLocal) {
    ArrayList<BeaconVersion> versions = new ArrayList<BeaconVersion>();
    versions.add(MakeBeaconVersion(ddbLocal));

    return SearchConfig.builder()
        .versions(versions)
        .writeVersion(1)
        .build();
  }

 public static IKeyring MakeHierarchicalKeyring(boolean ddbLocal)
  {
    final MaterialProviders matProv = MaterialProviders.builder()
      .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
      .build();

    final CreateAwsKmsHierarchicalKeyringInput keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
      .branchKeyId(BRANCH_KEY_ID)
      .keyStore(MakeKeyStore(ddbLocal))
      .ttlSeconds(6000l)
      .maxCacheSize(100)
      .build();
  
    return matProv.CreateAwsKmsHierarchicalKeyring(keyringInput);
  }

  public static DynamoDbEncryptionInterceptor MakeInterceptor(boolean ddbLocal)
  {
    final IKeyring kmsKeyring = MakeHierarchicalKeyring(ddbLocal);

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
        .search(MakeSearchConfig(ddbLocal))
        .build();

    HashMap<String, DynamoDbTableEncryptionConfig> tables = new HashMap<String, DynamoDbTableEncryptionConfig>();
    tables.put(TABLE_NAME, tableConfig);
    DynamoDbTablesEncryptionConfig config = DynamoDbTablesEncryptionConfig.builder()
        .tableEncryptionConfigs(tables)
        .build();

    return DynamoDbEncryptionInterceptor.builder().config(config).build();
  }
}

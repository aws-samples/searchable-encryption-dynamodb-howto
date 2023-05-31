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

  public static DynamoDbClientBuilder GetClientBuilder(boolean ddbLocal) {
    if (ddbLocal)
      return DynamoDbClient.builder()
          .endpointOverride(URI.create("http://localhost:8000"));
    else
      return DynamoDbClient.builder();
  }

  public static DynamoDbClient MakeDynamoDbClient(SharedOptions shared) {
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

  public static KeyStore MakeKeyStore(boolean ddbLocal) {
    return KeyStore.builder().KeyStoreConfig(
        KeyStoreConfig.builder()
            .ddbClient(GetClientBuilder(ddbLocal).build())
            .ddbTableName(BRANCH_KEY_TABLE)
            .logicalKeyStoreName(BRANCH_KEY_TABLE)
            .kmsClient(KmsClient.create())
            .kmsConfiguration(KMSConfiguration.builder()
                .kmsKeyArn(BRANCH_KEY_KMS_ARN)
                .build())
            .build())
        .build();
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
    beacons.add(StandardBeacon.builder()
        .name(MANAGER_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(BUILDING_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + BUILDING_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(CITY_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + CITY_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(FLOOR_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + FLOOR_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ROOM_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + ROOM_NAME)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(DESK_NAME)
        .length(8)
        .loc(LOCATION_NAME + "." + DESK_NAME)
        .build());
// BEGIN EXERCISE 4 STEP 2
    beacons.add(StandardBeacon.builder()
        .name(STATUS_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(ORGANIZER_EMAIL_NAME)
        .length(8)
        .build());
        beacons.add(StandardBeacon.builder()
        .name(ASSIGNEE_EMAIL_NAME)
        .length(8)
        .build());
        beacons.add(StandardBeacon.builder()
        .name(AUTHOR_EMAIL_NAME)
        .length(8)
        .build());
    beacons.add(StandardBeacon.builder()
        .name(SEVERITY_NAME)
        .length(8)
        .build());
// END EXERCISE 4 STEP 2

    return beacons;
  }

// BEGIN EXERCISE 4 STEP 3
public static Constructor MakeGsi1MeetingSortKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(START_TIME_NAME).required(true).build());
  parts.add(ConstructorPart.builder().name(FLOOR_NAME).required(true).build());
  parts.add(ConstructorPart.builder().name(ROOM_NAME).required(false).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi1ProjectPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(STATUS_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi1ReservationPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(ORGANIZER_EMAIL_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi3ReservationPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(BUILDING_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi1TicketPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(AUTHOR_EMAIL_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi1TicketSortKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(MODIFIED_DATE_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi2TicketPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(ASSIGNEE_EMAIL_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
public static Constructor MakeGsi3TicketPartitionKeyConstructor() {
  ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
  parts.add(ConstructorPart.builder().name(SEVERITY_NAME).required(true).build());
  return Constructor.builder().parts(parts).build();
}
// END EXERCISE 4 STEP 3

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

  public static Constructor MakeGsi1EmployeeSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(EMPLOYEE_NUMBER_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi2EmployeePartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(MANAGER_EMAIL_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi3EmployeePartitionKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(CITY_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static Constructor MakeGsi3EmployeeSortKeyConstructor() {
    ArrayList<ConstructorPart> parts = new ArrayList<ConstructorPart>();
    parts.add(ConstructorPart.builder().name(BUILDING_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(FLOOR_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(ROOM_NAME).required(true).build());
    parts.add(ConstructorPart.builder().name(DESK_NAME).required(true).build());
    return Constructor.builder().parts(parts).build();
  }

  public static CompoundBeacon MakeGsi2PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(MANAGER_EMAIL_NAME).prefix(MANAGER_EMAIL_PREFIX).build());
// BEGIN EXERCISE 4 STEP 4a
encryptedParts.add(EncryptedPart.builder().name(ASSIGNEE_EMAIL_NAME).prefix(ASSIGNEE_EMAIL_PREFIX).build());
// END EXERCISE 4 STEP 4a

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi2EmployeePartitionKeyConstructor());
// BEGIN EXERCISE 4 STEP 4b
    constructors.add(MakeGsi2TicketPartitionKeyConstructor());
// END EXERCISE 4 STEP 4b

    return CompoundBeacon.builder()
        .name(GSI2_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi3PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(CITY_NAME).prefix(CITY_PREFIX).build());
// BEGIN EXERCISE 4 STEP 5a
    encryptedParts.add(EncryptedPart.builder().name(BUILDING_NAME).prefix(BUILDING_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(SEVERITY_NAME).prefix(SEVERITY_PREFIX).build());
// END EXERCISE 4 STEP 5a

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi3EmployeePartitionKeyConstructor());
// BEGIN EXERCISE 4 STEP 5b
    constructors.add(MakeGsi3ReservationPartitionKeyConstructor());
    constructors.add(MakeGsi3TicketPartitionKeyConstructor());
// END EXERCISE 4 STEP 5b

    return CompoundBeacon.builder()
        .name(GSI3_PARTITION_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi3SortKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(BUILDING_NAME).prefix(BUILDING_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(FLOOR_NAME).prefix(FLOOR_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ROOM_NAME).prefix(ROOM_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(DESK_NAME).prefix(DESK_PREFIX).build());
// BEGIN EXERCISE 4 STEP 6a
    ArrayList<SignedPart> signedParts = new ArrayList<SignedPart>();
    signedParts.add(SignedPart.builder().name(MODIFIED_DATE_NAME).prefix(MODIFIED_DATE_PREFIX).build());
    signedParts.add(SignedPart.builder().name(START_TIME_NAME).prefix(START_TIME_PREFIX).build());
// END EXERCISE 4 STEP 6a

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi3EmployeeSortKeyConstructor());
// BEGIN EXERCISE 4 STEP 6b
    constructors.add(MakeGsi1TicketSortKeyConstructor()); // GSI3 same as GSI1
    constructors.add(MakeGsi1MeetingSortKeyConstructor()); // reservation GSI3 same as Meeting GSI1
// END EXERCISE 4 STEP 6b
    return CompoundBeacon.builder()
        .name(GSI3_SORT_KEY)
        .split(SPLIT)
        .encrypted(encryptedParts)
// BEGIN EXERCISE 4 STEP 6c
        .signed(signedParts)
// END EXERCISE 4 STEP 6c
        .constructors(constructors)
        .build();
  }

  public static CompoundBeacon MakeGsi1PartitionKey() {
    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(EMPLOYEE_EMAIL_NAME).prefix(EMPLOYEE_EMAIL_PREFIX).build());
// BEGIN EXERCISE 4 STEP 7a
    encryptedParts.add(EncryptedPart.builder().name(STATUS_NAME).prefix(STATUS_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ORGANIZER_EMAIL_NAME).prefix(ORGANIZER_EMAIL_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(AUTHOR_EMAIL_NAME).prefix(AUTHOR_EMAIL_PREFIX).build());
// END EXERCISE 4 STEP 7a

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardPartitionKeyConstructor());
    // Gsi1EmployeePartitionKey is the same as Gsi1TimecardPartitionKey
// BEGIN EXERCISE 4 STEP 7b
    constructors.add(MakeGsi1ProjectPartitionKeyConstructor());
    constructors.add(MakeGsi1ReservationPartitionKeyConstructor());
    constructors.add(MakeGsi1TicketPartitionKeyConstructor());
// END EXERCISE 4 STEP 7b

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
    signedParts.add(SignedPart.builder().name(EMPLOYEE_NUMBER_NAME).prefix(EMPLOYEE_NUMBER_PREFIX).build());
// BEGIN EXERCISE 4 STEP 8a
    signedParts.add(SignedPart.builder().name(MODIFIED_DATE_NAME).prefix(MODIFIED_DATE_PREFIX).build());

    ArrayList<EncryptedPart> encryptedParts = new ArrayList<EncryptedPart>();
    encryptedParts.add(EncryptedPart.builder().name(FLOOR_NAME).prefix(FLOOR_PREFIX).build());
    encryptedParts.add(EncryptedPart.builder().name(ROOM_NAME).prefix(ROOM_PREFIX).build());
// END EXERCISE 4 STEP 8a

    ArrayList<Constructor> constructors = new ArrayList<Constructor>();
    constructors.add(MakeGsi1TimecardSortKeyConstructor());
    constructors.add(MakeGsi1EmployeeSortKeyConstructor());
// BEGIN EXERCISE 4 STEP 8b
    constructors.add(MakeGsi1MeetingSortKeyConstructor());
    // ReservationSortKeyConstructor is the same as MeetingSortKeyConstructor
    constructors.add(MakeGsi1TicketSortKeyConstructor());
// END EXERCISE 4 STEP 8b

    return CompoundBeacon.builder()
        .name(GSI1_SORT_KEY)
        .split(SPLIT)
// BEGIN EXERCISE 4 STEP 8c
        .signed(signedParts)
// BEGIN EXERCISE 4 STEP 8c
        .encrypted(encryptedParts)
        .constructors(constructors)
        .build();
  }

  public static ArrayList<CompoundBeacon> MakeCompoundBeacons() {
    ArrayList<CompoundBeacon> beacons = new ArrayList<CompoundBeacon>();
    beacons.add(MakeGsi1PartitionKey());
    beacons.add(MakeGsi1SortKey());
    beacons.add(MakeGsi2PartitionKey());
    beacons.add(MakeGsi3PartitionKey());
    beacons.add(MakeGsi3SortKey());

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

  public static DynamoDbEncryptionInterceptor MakeInterceptor(boolean ddbLocal) {
    final MaterialProviders matProv = MaterialProviders.builder()
        .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
        .build();

    final CreateAwsKmsHierarchicalKeyringInput keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
        .branchKeyId(BRANCH_KEY_ID)
        .keyStore(MakeKeyStore(ddbLocal))
        .ttlSeconds(6000l)
        .maxCacheSize(100)
        .build();

    final IKeyring kmsKeyring = matProv.CreateAwsKmsHierarchicalKeyring(keyringInput);

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

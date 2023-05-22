// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.apache.commons.cli.*;

/**
 * Entry point for writing logic to work with the Document Bucket, with a helper to obtain an API
 * instance to use.
 */
public class App {

  // The names of resources from the configuration file must exactly match those
  // keys for the automatic mapping.
  // CHECKSTYLE:OFF AbbreviationAsWordInName

  /**
   * Obtain a Document Bucket API initialized with the resources as configured by the bootstrap
   * configuration system.
   *
   * @return a new {@link Api} configured automatically by the bootstrapping system.
   */
  public static Api initializeDocumentBucket() {
    // Load the TOML State file with the information about launched CloudFormation resources
    StateConfig stateConfig = new StateConfig(Config.contents.base.state_file);

    // Configure DynamoDB client
    String tableName = stateConfig.contents.state.DocumentTable;
    final DynamoDbClient ddbClient = DynamoDbClient.builder().build();

    // ADD-ESDK-START: Configure the Faythe KMS Key in the Encryption SDK
    return new Api(ddbClient, tableName);
  }
  // CHECKSTYLE:ON AbbreviationAsWordInName

  /**
   * Entry point for writing logic to interact with the Document Bucket system.
   *
   * @param args the command-line arguments to the Document Bucket.
   */
  public static void main(String[] args) {
    // Interact with the Document Bucket here or in jshell (mvn jshell:run)

    final HelpFormatter helper = new HelpFormatter();

    final Options options = new Options();
    options.addOption(Option
      .builder()
      .option("c")
      .longOpt("config")
      .desc("WTF???")
      .build());

    final CommandLineParser parser = new DefaultParser();

    try {
      CommandLine cmd = parser.parse(options, args);
      if (cmd.hasOption("c)")) {
        System.out.println("OMFG");
      }
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      helper.printHelp("Usage: ", options);
      System.exit(1);
    }
  }
}


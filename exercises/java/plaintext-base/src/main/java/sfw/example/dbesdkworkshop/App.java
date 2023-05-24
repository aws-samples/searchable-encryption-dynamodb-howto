// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.net.URI;

/**
 * Entry point for writing logic to work with the Employee Portal,
 * with a helper to obtain an API instance to use.
 */

@Command(
  name = "Employee Portal",
  description = "Entrypoint for interacting with the employee portal",
  subcommands = {
    CreateTable.class,
    GetEmployees.class,
    GetMeetings.class,
    PutMeeting.class,
    PutEmployee.class,
    PutProject.class,
    PutReservation.class,
    PutTicket.class,
    PutTimecard.class,
    CommandLine.HelpCommand.class
  }
)
public class App {

  // The names of resources from the configuration file must exactly match those
  // keys for the automatic mapping.
  // CHECKSTYLE:OFF AbbreviationAsWordInName

  /**
   * Obtain an Employee Portal API initialized with the resources as configured
   * by the bootstrap configuration system.
   *
   * @return a new {@link Api} configured automatically by the bootstrapping system.
   */
  public static Api initializeEmployeePortal() {
    // Load the TOML State file with the information about launched CloudFormation resources
    // StateConfig stateConfig = new StateConfig(Config.contents.base.state_file);

    // Configure DynamoDB client
    // String tableName = stateConfig.contents.state.DocumentTable;
    String tableName = "MyTestTable";
    final DynamoDbClient ddbClient = DynamoDbClient.builder().endpointOverride(URI.create("http://localhost:8000")).build();

    // ADD-ESDK-START: Configure the Faythe KMS Key in the Encryption SDK
    return new Api(ddbClient, tableName);
  }
  // CHECKSTYLE:ON AbbreviationAsWordInName

  /**
   * Entry point for writing logic to interact with the Employee Portal.
   *
   * @param args the command-line arguments to the Document Bucket.
   */
  public static void main(String[] args) {
    //System.out.println("Something else");
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}

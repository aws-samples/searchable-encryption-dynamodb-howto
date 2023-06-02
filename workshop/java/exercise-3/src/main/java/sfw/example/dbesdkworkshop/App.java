// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import static sfw.example.dbesdkworkshop.AwsSupport.*;
import static sfw.example.dbesdkworkshop.Config.Constants.*;

/**
 * Entry point for writing logic to work with the Employee Portal,
 * with a helper to obtain an API instance to use.
 */

@Command(
  name = "employee-portal",
  description = "Entrypoint for interacting with the employee portal",
  subcommands = {
    CreateBranchKey.class,
    CreateTable.class,
    DeleteTable.class,
    GetEmployees.class,
    GetMeetings.class,
    GetProjects.class,
    GetReservations.class,
    GetTickets.class,
    GetTimecards.class,
    PutMeeting.class,
    PutEmployee.class,
    PutProject.class,
    PutReservation.class,
    PutTicket.class,
    PutTimecard.class,
    ScanTable.class,
    CommandLine.HelpCommand.class,
    picocli.AutoComplete.GenerateCompletion.class
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
  public static Api initializeEmployeePortal(SharedOptions shared) {
      return new Api(MakeDynamoDbClient(shared), TABLE_NAME);
    }
  
  // CHECKSTYLE:ON AbbreviationAsWordInName

  /**
   * Entry point for writing logic to interact with the Employee Portal.
   *
   * @param args the command-line arguments to the Document Bucket.
   */
  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}

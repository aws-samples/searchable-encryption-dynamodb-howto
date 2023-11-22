package sfw.example.dbesdkworkshop;

import picocli.CommandLine;

public class SharedOptions {
  @CommandLine.Option( names = {"-l", "--ddb-local"}, description = "use ddb local client")
  boolean ddbLocal = false;
  @CommandLine.Option( names = {"-L", "--plain"}, description = "use a plain DynamoDB client, not the DBE SDK.")
  boolean plain = false;
}

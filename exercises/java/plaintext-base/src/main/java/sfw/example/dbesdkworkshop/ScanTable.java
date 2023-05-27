package sfw.example.dbesdkworkshop;

import java.util.List;
import java.util.Map;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import software.amazon.awssdk.services.dynamodb.model.*;

@Command(name = "scan-table", description = "Scans the table.")
public class ScanTable implements Runnable {
  @Option(names = {"-v", "--vanilla"}, description = "use vanilla client")
  boolean vanilla;

  @Override
  public void run() {
    Api api;
    if (vanilla) 
      api = App.initializeEmployeePortalVanilla();
    else
      api = App.initializeEmployeePortal();
    List<Map<String,AttributeValue>> results = api.scanTable();
    for (Map<String,AttributeValue> item : results)
      System.out.println(item);
  }
}

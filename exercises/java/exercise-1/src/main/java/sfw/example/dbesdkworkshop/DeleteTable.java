package sfw.example.dbesdkworkshop;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "delete-table", description = "Deletes the table.")
public class DeleteTable implements Runnable {

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal();
    api.deleteTable();

    System.out.println("Table Deleted");
  }
}

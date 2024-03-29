package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "create-table", description = "Creates the table.")
public class CreateTable implements Runnable {

  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    final Api api = App.initializeEmployeePortal(shared);
    api.createTable();

    System.out.println("Table Created");
  }
}

package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.AwsSupport;

@Command(name = "create-branch-key", description = "Creates a branch key.")
public class CreateBranchKey implements Runnable {

  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    String keyId = AwsSupport.CreateBranchKey();

    System.out.println("Created branch key : " + keyId);
  }
}

package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.AwsSupport;
import software.amazon.cryptography.keystore.KeyStore;
import software.amazon.cryptography.keystore.model.CreateKeyStoreInput;

import static sfw.example.dbesdkworkshop.AwsSupport.MakeKeyStore;

@Command(name = "create-branch-key", description = "Creates a branch key.")
public class CreateBranchKey implements Runnable {

  @CommandLine.Option( names = {"-t", "--only-table"}, description = "only create the branch key table")
  boolean onlyTable = false;

  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {
    // To bootstrap the workshop it is nice to be able to create the keystore table independently
    if (onlyTable) {
      final KeyStore keystore = MakeKeyStore(shared.ddbLocal);
      keystore.CreateKeyStore(CreateKeyStoreInput.builder().build());
      System.out.println("Branch key table created");
    } else {
      String keyId = AwsSupport.CreateBranchKey(shared.ddbLocal);
      System.out.println("Created branch key : " + keyId);
    }
  }
}

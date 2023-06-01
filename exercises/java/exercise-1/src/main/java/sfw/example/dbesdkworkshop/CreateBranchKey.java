package sfw.example.dbesdkworkshop;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import sfw.example.dbesdkworkshop.AwsSupport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Command(name = "create-branch-key", description = "Creates a branch key.")
public class CreateBranchKey implements Runnable {

  @CommandLine.Mixin
  SharedOptions shared = new SharedOptions();

  @Override
  public void run() {

    // Given the order of the steps in the workshop
    // This needs to be able to compile _before_ it can work.
    // This code is conveyance to make the steps flow
    // it is not intended as a good example of how to call a function.

    try {
      final Method method = AwsSupport.class.getDeclaredMethod("CreateBranchKey");
      final Object result = method.invoke(null, shared.ddbLocal);
      final String keyId = (String) result;
      System.out.println("Created branch key : " + keyId);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }
}

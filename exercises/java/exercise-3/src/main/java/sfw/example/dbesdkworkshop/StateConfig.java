// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import com.moandjiezana.toml.Toml;
import java.io.File;

/** Helper to pull required Document Bucket configuration keys out of the configuration system. */
public class StateConfig {

  // This is just a way to share information across processes
  // This is not portable, but it works for the workshop
  // This way the various script files can be executed from any path
  // Another solution might be to add this to the jar
  // and build the toml into the META-INF
  // but this might make for confusing state.
  private static final File JAR_PATH = new File(StateConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath());
  public static final File DEFAULT_CONFIG = new File(JAR_PATH.getParent(), "../../../../config.toml");
  public static final ConfigContents contents =
    new Toml().read(DEFAULT_CONFIG).to(ConfigContents.class);

  private StateConfig() { // Do not instantiate
  }

  // For automatic mpaping, these classes all have names dictated by the TOML file.
  // CHECKSTYLE:OFF MemberName
  // CHECKSTYLE:OFF ParameterName

  /** The top-level contents of the configuration file. */
  public static class ConfigContents {
    /** The [base] section of the configuration file. */
    public final WrappingKeyInfo wrapping_key_info;

    ConfigContents(WrappingKeyInfo base) {
      this.wrapping_key_info = base;
    }
  }

  /** The [base] section of the configuration file. */
  public static class WrappingKeyInfo {
    /** The location of the state file for CloudFormation-managed AWS resource identifiers. */
    public final String branch_key_table;
    public final String branch_key_kms_arn;
    public final String branch_key_id;

    WrappingKeyInfo(
      String branch_key_table,
      String branch_key_kms_arn,
      String branch_key_id
    ) {
      this.branch_key_table = branch_key_table;
      this.branch_key_kms_arn = branch_key_kms_arn;
      this.branch_key_id = branch_key_id;
    }
  }

  // CHECKSTYLE:ON MemberName
  // CHECKSTYLE:ON ParameterName
}

// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

import com.moandjiezana.toml.Toml;
import java.io.File;

/** Helper to pull required Document Bucket configuration keys out of the configuration system. */
public class Config {
  private static final File DEFAULT_CONFIG = new File("../../config.toml");
  public static final ConfigContents contents = new Toml().read(DEFAULT_CONFIG).to(ConfigContents.class);

//    new ConfigContents(
//      new Base("MyStateFile"),
//      new DocumentBucket(new DocumentTable("MyTestTable", "PartKey", "SortKey", "4", "5"), new Bucket("", "", "")));
      // new Toml().read(DEFAULT_CONFIG).to(ConfigContents.class);

  private Config() { // Do not instantiate
  }

  // For automatic mpaping, these classes all have names dictated by the TOML file.
  // CHECKSTYLE:OFF MemberName
  // CHECKSTYLE:OFF ParameterName

  /** The top-level contents of the configuration file. */
  public static class ConfigContents {
    /** The [base] section of the configuration file. */
    public final Base base;
    /** The [document_bucket] section of the configuration file. */
    public final DDBTable ddb_table;

    ConfigContents(Base base, DDBTable ddb_table) {
      this.base = base;
      this.ddb_table = ddb_table;
    }
  }

  /** The [base] section of the configuration file. */
  public static class Base {
    /** The location of the state file for CloudFormation-managed AWS resource identifiers. */
    public final String state_file;

    Base(String state_file) {
      this.state_file = state_file;
    }
  }

  /** The [ddb_table] section of the configuration file. */
  public static class DDBTable {
    /** Table name. */
    public final String name;

    /** Table partition key name. */
    public final String partition_key;

    /** Table sort key name. */
    public final String sort_key;

    /** Table GSI1 name. */
    public final String gsi1_name;
    /** Table GSI1 partition key name. */
    public final String gsi1_partition_key;
    /** Table GSI1 sort key name. */
    public final String gsi1_sort_key;

    /** Table GSI2 name. */
    public final String gsi2_name;
    /** Table GSI2 partition key name. */
    public final String gsi2_partition_key;
    /** Table GSI2 sort key is the primary sort key. */

    /** Table GSI3 name. */
    public final String gsi3_name;
    /** Table GSI3 partition key name. */
    public final String gsi3_partition_key;
    /** Table GSI3 sort key name. */
    public final String gsi3_sort_key;

    DDBTable(
        String name,
        String partition_key,
        String sort_key,
        String gsi1_name,
        String gsi1_partition_key,
        String gsi1_sort_key,
        String gsi2_name,
        String gsi2_partition_key,
        String gsi3_name,
        String gsi3_partition_key,
        String gsi3_sort_key
    ) {
      this.name = name;
      this.partition_key = partition_key;
      this.sort_key = sort_key;
      this.gsi1_name = gsi1_name;
      this.gsi1_partition_key = gsi1_partition_key;
      this.gsi1_sort_key = gsi1_sort_key;
      this.gsi2_name = gsi2_name;
      this.gsi2_partition_key = gsi2_partition_key;
      this.gsi3_name = gsi3_name;
      this.gsi3_partition_key = gsi3_partition_key;
      this.gsi3_sort_key = gsi3_sort_key;
    }
  }

  // CHECKSTYLE:ON MemberName
  // CHECKSTYLE:ON ParameterName
}

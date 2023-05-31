// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

/** Helper to pull required Document Bucket configuration keys out of the configuration system. */
public class Config {
  private Config() { // Do not instantiate
  }

  // For automatic mapping, these classes all have names dictated by the TOML file.
  // CHECKSTYLE:OFF MemberName
  // CHECKSTYLE:OFF ParameterName

  public static class Constants {
    public static final boolean USE_LOCAL_DDB = true;
    public static final String BEACON_PREFIX = "aws_dbe_b_";
    public static final String SPLIT = "^";

    // BEGIN EXERCISE 3 STEP 1
    public static final String TABLE_NAME = "Exercise3_Table";
    // END EXERCISE 3 STEP 1
    public static final String BRANCH_KEY_TABLE = "BranchKey_Table";
    public static final String BRANCH_KEY_KMS_ARN = "arn:aws:kms:us-west-2:370957321024:key/9d989aa2-2f9c-438c-a745-cc57d3ad0126";
    public static final String BRANCH_KEY_ID = "63ce6ee1-254b-41f3-82a9-051ea730182a";

    public static final String GSI1_NAME = "GSI1";
    public static final String GSI2_NAME = "GSI2";
    public static final String GSI3_NAME = "GSI3";

    public static final String PARTITION_KEY = "PK";
    public static final String SORT_KEY = "SK";

    public static final String GSI1_PARTITION_KEY = "PK1";
    public static final String GSI1_SORT_KEY = "SK1";
    public static final String GSI2_PARTITION_KEY = "PK2";
    public static final String GSI3_PARTITION_KEY = "PK3";
    public static final String GSI3_SORT_KEY = "SK3";

    public static final String ASSIGNEE_EMAIL_NAME = "assigneeEmail";
    public static final String ASSIGNEE_EMAIL_PREFIX = "AE-";
    public static final String AUTHOR_EMAIL_NAME = "authorEmail";
    public static final String AUTHOR_EMAIL_PREFIX = "CE-";
    public static final String EMPLOYEE_EMAIL_NAME = "employeeEmail";
    public static final String EMPLOYEE_EMAIL_PREFIX = "EE-";
    public static final String EMPLOYEE_NUMBER_NAME = "employeeNumber";
    public static final String EMPLOYEE_NUMBER_PREFIX = "E-";
    public static final String MODIFIED_DATE_NAME = "modifiedDate";
    public static final String MODIFIED_DATE_PREFIX = "M-";
    public static final String MANAGER_EMAIL_NAME = "managerEmail";
    public static final String MANAGER_EMAIL_PREFIX = "ME-";
    public static final String ORGANIZER_EMAIL_NAME = "organizerEmail";
    public static final String ORGANIZER_EMAIL_PREFIX = "OE-";
    public static final String PROJECT_NAME_NAME = "projectName";
    public static final String PROJECT_NAME_PREFIX = "P-";
    public static final String START_TIME_NAME = "startTime";
    public static final String START_TIME_PREFIX = "S-";
    public static final String TICKET_NUMBER_NAME = "ticketNumber";
    public static final String TICKET_NUMBER_PREFIX = "T-";
    public static final String STATUS_NAME = "status";
    public static final String STATUS_PREFIX = "U-";
    public static final String RESERVATION_NAME = "reservation";
    public static final String RESERVATION_PREFIX = "V-";
    public static final String SEVERITY_NAME = "severity";
    public static final String SEVERITY_PREFIX = "Y-";
    
    public static final String CITY_PREFIX = "C-";
    public static final String BUILDING_PREFIX = "B-";
    public static final String DESK_PREFIX = "D-";
    public static final String FLOOR_PREFIX = "F-";
    public static final String ROOM_PREFIX = "R-";

    public static final String CITY_NAME = "city";
    public static final String BUILDING_NAME = "building";
    public static final String DESK_NAME = "desk";
    public static final String FLOOR_NAME = "floor";
    public static final String ROOM_NAME = "room";

    // the token "role" is special in DynamoDB, hence "empRole"
    public static final String ROLE_NAME = "empRole";
    public static final String LOCATION_NAME = "location";
    public static final String EMPLOYEE_NAME_NAME = "name";
    public static final String TITLE_NAME = "title";
    public static final String DURATION_NAME = "duration";
    public static final String ATTENDEES_NAME = "attendees";
    public static final String SUBJECT_NAME = "subject";
    public static final String MESSAGE_NAME = "message";
    public static final String DESCRIPTION_NAME = "description";
    public static final String TARGET_DATE_NAME = "targetDate";
    public static final String HOURS_NAME = "hours";
  }
  // CHECKSTYLE:ON MemberName
  // CHECKSTYLE:ON ParameterName
}

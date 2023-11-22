// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package sfw.example.dbesdkworkshop;

/** Exception to wrap errors encountered during Document Bucket operations. */
public class EmployeePortalException extends RuntimeException {
  public EmployeePortalException(String message, Throwable cause) {
    super(message, cause);
  }
}

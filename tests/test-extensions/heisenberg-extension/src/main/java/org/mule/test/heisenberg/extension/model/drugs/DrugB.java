/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.heisenberg.extension.model.drugs;

import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.Map;

public class DrugB {

  @Parameter
  Map<String, String> properties;

  public DrugB() {}

  public DrugB(Map<String, String> properties) {
    this.properties = properties;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }
}

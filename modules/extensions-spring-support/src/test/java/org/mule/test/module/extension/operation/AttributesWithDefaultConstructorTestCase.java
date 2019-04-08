/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.module.extension.operation;

import org.mule.runtime.api.message.Message;
import org.mule.test.module.extension.AbstractExtensionFunctionalTestCase;

import org.junit.Test;

public class AttributesWithDefaultConstructorTestCase extends AbstractExtensionFunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "attributes-with-default-constructor.xml";
  }

  @Test
  public void getPropertiesTestCase() throws Exception {
    Message message = flowRunner("get-properties").run().getMessage();
  }

}

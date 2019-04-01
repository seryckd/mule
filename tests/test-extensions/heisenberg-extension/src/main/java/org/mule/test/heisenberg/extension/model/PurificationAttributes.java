/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.heisenberg.extension.model;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public class PurificationAttributes {

  @Parameter
  private final Integer cost;

  @Parameter
  private final String chemist;

  @Parameter
  private final String requester;

  public PurificationAttributes() {
    this(null, null, null);
  }

  public PurificationAttributes(Integer cost, String chemist, String requester) {
    this.cost = cost;
    this.chemist = chemist;
    this.requester = requester;
  }
}

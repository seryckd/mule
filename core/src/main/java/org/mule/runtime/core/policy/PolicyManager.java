/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.policy;

import org.mule.runtime.core.api.Event;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.dsl.api.component.config.ComponentIdentifier;

import java.util.Map;

/**
 * Manager for handling policies in Mule.
 * 
 * Implementation of this class will be used to lookup for {@code Policy}s that must be applied to {@code MessageSource}es or
 * {@code Processor}s.
 * 
 * @since 4.0
 */
public interface PolicyManager {

  /**
   * Creates a policy to be applied to a source. The creation must have into consideration the {@code sourceIdentifier} to find specific
   * policies applied to that source and also the {@code sourceEvent} which will be used to extract data to match against the policies
   * pointcuts.
   *
   * @param sourceIdentifier the source identifier.
   * @param sourceEvent the event generated from the source.
   * @param flowExecutionProcessor the processor that executes the flow.
   * @param messageSourceResponseParametersProcessor processor to generate the response and error response parameters of the source.
   * @return a {@link SourcePolicy} associated to that source.
   */
  SourcePolicy createSourcePolicyInstance(ComponentIdentifier sourceIdentifier, Event sourceEvent,
                                          Processor flowExecutionProcessor,
                                          MessageSourceResponseParametersProcessor messageSourceResponseParametersProcessor);

  /**
   * Creates a policy to be applied to an operation. The creation must have into consideration the {@code operationIdentifier} to find specific
   * policies applied to that operation and also the {@code operationParameters} which will be used to extract data to match against the policies
   * pointcuts.
   *
   * @param operationIdentifier component identifier of the operation.
   * @param operationEvent the event used to execute the operation.
   * @param operationParameters the set of parameters to use to execute the operation.
   * @param operationExecutionFunction the function that executes the operation.
   * @return a {@link OperationPolicy} associated to that source.
   */
  OperationPolicy createOperationPolicy(ComponentIdentifier operationIdentifier, Event operationEvent,
                                        Map<String, Object> operationParameters,
                                        OperationExecutionFunction operationExecutionFunction);

  /**
   * Disposes any resource associated to the given {@code executionIdentifier}. Any exception generated by this method will be
   * just logged and discarded.
   *
   * @param executionIdentifier identifier used for previous policies executions.
   */
  void disposePoliciesResources(String executionIdentifier);

}

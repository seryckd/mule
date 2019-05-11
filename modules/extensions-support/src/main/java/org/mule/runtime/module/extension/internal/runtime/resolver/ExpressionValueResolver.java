/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.resolver;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import static org.mule.runtime.core.api.config.MuleProperties.COMPATIBILITY_PLUGIN_INSTALLED;
import static org.mule.runtime.core.api.config.MuleProperties.MULE_MEL_AS_DEFAULT;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.api.util.ClassUtils.isInstance;
import static org.mule.runtime.core.internal.el.DefaultExpressionManager.hasDwExpression;
import static org.mule.runtime.core.internal.el.DefaultExpressionManager.hasMelExpression;
import org.mule.runtime.api.artifact.Registry;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.el.ExtendedExpressionManager;
import org.mule.runtime.core.api.util.func.Once;
import org.mule.runtime.core.api.util.func.Once.RunOnce;
import org.mule.runtime.core.privileged.util.AttributeEvaluator;

import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * A {@link ValueResolver} which evaluates a MEL expressions
 * <p>
 * It resolves the expressions by making use of the {@link AttributeEvaluator} so that it's compatible with simple expressions and
 * templates alike
 *
 * @param <T>
 * @since 4.0
 */
public class ExpressionValueResolver<T> implements ExpressionBasedValueResolver<T>, Initialisable {

  private static final String PAYLOAD_EXPRESSION = "#[payload]";
  private static final String ATTRIBUTES_EXPRESSION = "#[attributes]";
  private static final ResolverFunction PAYLOAD_FUNCTION = new PayloadResolverFunction();
  private static final ResolverFunction ATTRIBUTES_FUNCTION = new AttributesResolverFunction();

  @Inject
  private ExtendedExpressionManager extendedExpressionManager;

  @Inject
  private Registry registry;

  final AttributeEvaluator evaluator;
  private final String expression;
  private Function<ValueResolvingContext, TypedValue> resolverFunction;

  private final RunOnce evaluatorInitialiser = Once.of(() -> {
    initialiseIfNeeded(extendedExpressionManager);
    getEvaluator().initialize(extendedExpressionManager);
  });

  private Boolean melDefault;
  private Boolean melAvailable;

  ExpressionValueResolver(String expression, DataType expectedDataType) {
    checkArgument(!StringUtils.isBlank(expression), "Expression cannot be blank or null");
    this.expression = expression;
    this.evaluator = new AttributeEvaluator(expression, expectedDataType);
  }

  public ExpressionValueResolver(String expression, DataType expectedDataType, Boolean melDefault, Boolean melAvailable) {
    this(expression, expectedDataType);
    this.melDefault = melDefault;
    this.melAvailable = melAvailable;
  }

  public ExpressionValueResolver(String expression) {
    checkArgument(!StringUtils.isBlank(expression), "Expression cannot be blank or null");
    this.expression = expression;
    this.evaluator = new AttributeEvaluator(expression);

  }

  void setExtendedExpressionManager(ExtendedExpressionManager extendedExpressionManager) {
    this.extendedExpressionManager = extendedExpressionManager;
  }

  @Override
  public void initialise() throws InitialisationException {
    initEvaluator();
    if (melDefault == null) {
      melDefault = valueOf(getProperty(MULE_MEL_AS_DEFAULT, "false"));
    }

    if (melAvailable == null) {
      melAvailable = registry.lookupByName(COMPATIBILITY_PLUGIN_INSTALLED).isPresent();
    }

    resolverFunction = getResolverFunction(expression);
  }

  private ResolverFunction getResolverFunction(String expression) {
    if (PAYLOAD_EXPRESSION.equals(expression)) {
      return PAYLOAD_FUNCTION;
    } else if (ATTRIBUTES_EXPRESSION.equals(expression)) {
      return ATTRIBUTES_FUNCTION;
    } else {
      if (isMelAvailable() &&
          (!hasDwExpression(expression) && !hasMelExpression(expression) && melDefault)
          || hasMelExpression(expression)) {
        return new MELResolverFunction(evaluator);
      } else {
        return new DWResolverFunction(evaluator);
      }
    }
  }

  @Override
  public T resolve(ValueResolvingContext context) throws MuleException {
    TypedValue<T> typedValue = resolveTypedValue(context);

    Object value = typedValue.getValue();

    if (isInstance(ValueResolver.class, value)) {
      value = ((ValueResolver) value).resolve(context);
    }

    return (T) value;
  }

  protected <V> TypedValue<V> resolveTypedValue(ValueResolvingContext context) {
    return resolverFunction.apply(context);
  }

  void initEvaluator() {
    evaluatorInitialiser.runOnce();
  }

  /**
   * @return {@code true}
   */
  @Override
  public boolean isDynamic() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExpression() {
    return expression;
  }

  public boolean isMelAvailable() {
    return melAvailable;
  }

  public void setRegistry(Registry registry) {
    this.registry = registry;
  }

  private AttributeEvaluator getEvaluator() {
    return evaluator;
  }

  private interface ResolverFunction extends Function<ValueResolvingContext, TypedValue> {

  }


  private static class PayloadResolverFunction implements ResolverFunction {

    @Override
    public TypedValue apply(ValueResolvingContext valueResolvingContext) {
      return valueResolvingContext.getEvent().getMessage().getPayload();
    }
  }


  private static class AttributesResolverFunction implements ResolverFunction {

    @Override
    public TypedValue apply(ValueResolvingContext valueResolvingContext) {
      return valueResolvingContext.getEvent().getMessage().getAttributes();
    }
  }


  private static class DWResolverFunction implements ResolverFunction {

    private final AttributeEvaluator evaluator;

    private DWResolverFunction(AttributeEvaluator evaluator) {
      this.evaluator = evaluator;
    }

    @Override
    public TypedValue apply(ValueResolvingContext context) {
      if (context.getSession() != null) {
        return evaluator.resolveTypedValue(context.getSession());
      } else {
        return evaluator.resolveTypedValue(context.getEvent());
      }
    }
  }


  private static class MELResolverFunction implements ResolverFunction {

    private final AttributeEvaluator evaluator;

    private MELResolverFunction(AttributeEvaluator evaluator) {
      this.evaluator = evaluator;
    }

    @Override
    public TypedValue apply(ValueResolvingContext context) {
      return evaluator.resolveTypedValue(context.getEvent());
    }
  }
}

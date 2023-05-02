/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.generator.spring.utils;

import com.google.api.generator.engine.ast.ArithmeticOperationExpr;
import com.google.api.generator.engine.ast.AssignmentExpr;
import com.google.api.generator.engine.ast.ConcreteReference;
import com.google.api.generator.engine.ast.Expr;
import com.google.api.generator.engine.ast.ExprStatement;
import com.google.api.generator.engine.ast.IfStatement;
import com.google.api.generator.engine.ast.MethodInvocationExpr;
import com.google.api.generator.engine.ast.ScopeNode;
import com.google.api.generator.engine.ast.Statement;
import com.google.api.generator.engine.ast.TypeNode;
import com.google.api.generator.engine.ast.Variable;
import com.google.api.generator.engine.ast.VariableExpr;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerUtils {

  private static final Map<String, TypeNode> STATIC_TYPES = createStaticTypes();

  public static Statement getLoggerDeclarationExpr(String className, Map<String, TypeNode> types) {

    Variable loggerVar =
        Variable.builder().setName("LOGGER").setType(STATIC_TYPES.get("Log")).build();
    VariableExpr loggerExpr =
        VariableExpr.builder()
            .setVariable(loggerVar)
            .setScope(ScopeNode.PRIVATE)
            .setIsStatic(true)
            .setIsFinal(true)
            .setIsDecl(true)
            .build();

    MethodInvocationExpr loggerValueExpr =
        MethodInvocationExpr.builder()
            .setStaticReferenceType(STATIC_TYPES.get("LogFactory"))
            .setMethodName("getLog")
            .setArguments(
                VariableExpr.builder()
                    .setVariable(
                        Variable.builder().setType(TypeNode.CLASS_OBJECT).setName("class").build())
                    .setStaticReferenceType(types.get(className))
                    .build())
            .setReturnType(STATIC_TYPES.get("Log"))
            .build();

    AssignmentExpr loggerAssignmentExpr =
        AssignmentExpr.builder().setVariableExpr(loggerExpr).setValueExpr(loggerValueExpr).build();

    return ExprStatement.withExpr(loggerAssignmentExpr);
  }

  public static Statement createLoggerStatement(Expr value, Map<String, TypeNode> types) {
    Variable loggerVariable =
        Variable.builder().setName("LOGGER").setType(STATIC_TYPES.get("Log")).build();
    MethodInvocationExpr loggerCallExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(loggerVariable))
            .setMethodName("trace")
            .setArguments(value)
            .build();
    return IfStatement.builder()
        .setConditionExpr(
            MethodInvocationExpr.builder()
                .setExprReferenceExpr(VariableExpr.withVariable(loggerVariable))
                .setMethodName("isTraceEnabled")
                .setReturnType(TypeNode.BOOLEAN)
                .build())
        .setBody(Arrays.asList(ExprStatement.withExpr(loggerCallExpr)))
        .build();
  }

  public static Expr concatManyWithExprs(Expr... exprs) {
    List<Expr> exprList = Arrays.asList(exprs);
    return concatManyWithExprsHelper(Optional.empty(), exprList);
  }

  private static Expr concatManyWithExprsHelper(Optional<Expr> current, List<Expr> exprs) {
    if (!current.isPresent()) {
      return concatManyWithExprsHelper(Optional.of(exprs.get(0)), exprs.subList(1, exprs.size()));
    }
    if (exprs.size() == 1) {
      return ArithmeticOperationExpr.concatWithExprs(current.get(), exprs.get(0));
    }
    return ArithmeticOperationExpr.concatWithExprs(
        current.get(),
        concatManyWithExprsHelper(Optional.of(exprs.get(0)), exprs.subList(1, exprs.size())));
  }

  private static Map<String, TypeNode> createStaticTypes() {
    List<Class> concreteClazzes = Arrays.asList(Log.class, LogFactory.class);
    return concreteClazzes.stream()
        .collect(
            Collectors.toMap(
                Class::getSimpleName, c -> TypeNode.withReference(ConcreteReference.withClazz(c))));
  }
}

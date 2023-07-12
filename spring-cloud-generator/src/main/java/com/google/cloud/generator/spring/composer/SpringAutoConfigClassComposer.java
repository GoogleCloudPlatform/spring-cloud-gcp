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

package com.google.cloud.generator.spring.composer;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.httpjson.InstantiatingHttpJsonChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.generator.engine.ast.AnnotationNode;
import com.google.api.generator.engine.ast.ArithmeticOperationExpr;
import com.google.api.generator.engine.ast.AssignmentExpr;
import com.google.api.generator.engine.ast.CastExpr;
import com.google.api.generator.engine.ast.ClassDefinition;
import com.google.api.generator.engine.ast.ConcreteReference;
import com.google.api.generator.engine.ast.EmptyLineStatement;
import com.google.api.generator.engine.ast.Expr;
import com.google.api.generator.engine.ast.ExprStatement;
import com.google.api.generator.engine.ast.IfStatement;
import com.google.api.generator.engine.ast.LambdaExpr;
import com.google.api.generator.engine.ast.MethodDefinition;
import com.google.api.generator.engine.ast.MethodInvocationExpr;
import com.google.api.generator.engine.ast.NewObjectExpr;
import com.google.api.generator.engine.ast.PrimitiveValue;
import com.google.api.generator.engine.ast.RelationalOperationExpr;
import com.google.api.generator.engine.ast.ReturnExpr;
import com.google.api.generator.engine.ast.ScopeNode;
import com.google.api.generator.engine.ast.Statement;
import com.google.api.generator.engine.ast.StringObjectValue;
import com.google.api.generator.engine.ast.ThisObjectValue;
import com.google.api.generator.engine.ast.TypeNode;
import com.google.api.generator.engine.ast.ValueExpr;
import com.google.api.generator.engine.ast.VaporReference;
import com.google.api.generator.engine.ast.Variable;
import com.google.api.generator.engine.ast.VariableExpr;
import com.google.api.generator.gapic.composer.common.ClassComposer;
import com.google.api.generator.gapic.composer.utils.ClassNames;
import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicClass.Kind;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicServiceConfig;
import com.google.api.generator.gapic.model.Method;
import com.google.api.generator.gapic.model.Service;
import com.google.api.generator.gapic.model.Transport;
import com.google.api.generator.gapic.utils.JavaStyle;
import com.google.cloud.generator.spring.composer.comment.SpringAutoconfigCommentComposer;
import com.google.cloud.generator.spring.utils.ComposerUtils;
import com.google.cloud.generator.spring.utils.LoggerUtils;
import com.google.cloud.generator.spring.utils.Utils;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.common.base.CaseFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

public class SpringAutoConfigClassComposer implements ClassComposer {
  private static final SpringAutoConfigClassComposer INSTANCE = new SpringAutoConfigClassComposer();

  private static final Map<String, TypeNode> STATIC_TYPES = createStaticTypes();
  private static final Statement EMPTY_LINE_STATEMENT = EmptyLineStatement.create();

  private SpringAutoConfigClassComposer() {}

  public static SpringAutoConfigClassComposer instance() {
    return INSTANCE;
  }

  @Override
  public GapicClass generate(GapicContext context, Service service) {
    String packageName = Utils.getSpringPackageName(service.pakkage());
    Map<String, TypeNode> dynamicTypes = createDynamicTypes(service, packageName);
    String serviceName = service.name();
    String serviceNameLowerCamel = JavaStyle.toLowerCamelCase(serviceName);
    String serviceNameLowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, serviceName);
    String className = Utils.getServiceAutoConfigurationClassName(service);
    String credentialsProviderName = serviceNameLowerCamel + "Credentials";
    String transportChannelProviderName = "default" + serviceName + "TransportChannelProvider";
    String clientName = serviceNameLowerCamel + "Client";
    GapicClass.Kind kind = Kind.MAIN;

    GapicServiceConfig gapicServiceConfig = context.serviceConfig();
    Transport transport = context.transport();

    Expr thisExpr = ValueExpr.withValue(ThisObjectValue.withType(dynamicTypes.get(className)));
    String serviceSettingsMethodName = JavaStyle.toLowerCamelCase(service.name()) + "Settings";

    ClassDefinition classDef =
        ClassDefinition.builder()
            .setPackageString(packageName)
            .setName(className)
            .setScope(ScopeNode.PUBLIC)
            .setHeaderCommentStatements(
                SpringAutoconfigCommentComposer.createClassHeaderComments(service))
            .setStatements(
                createMemberVariables(service, packageName, dynamicTypes, gapicServiceConfig))
            .setAnnotations(createClassAnnotations(service, dynamicTypes))
            .setMethods(
                Arrays.asList(
                    createConstructor(service, className, dynamicTypes, thisExpr),
                    createTransportChannelProviderBeanMethod(
                        service,
                        transport,
                        transportChannelProviderName,
                        dynamicTypes,
                        thisExpr),
                    createSettingsBeanMethod(
                        service,
                        transport,
                        transportChannelProviderName,
                        dynamicTypes,
                        thisExpr,
                        serviceSettingsMethodName),
                    createClientBeanMethod(dynamicTypes, service, serviceSettingsMethodName),
                    createUserAgentHeaderProviderMethod(
                        serviceNameLowerHyphen, className, dynamicTypes, thisExpr)))
            .build();

    return GapicClass.create(kind, classDef);
  }

  private static List<Statement> createMemberVariables(
      Service service,
      String packageName,
      Map<String, TypeNode> types,
      GapicServiceConfig serviceConfig) {

    // Create clientProperties variable
    ExprStatement clientPropertiesStatement =
        ComposerUtils.createMemberVarStatement(
            "clientProperties",
            types.get(Utils.getServicePropertiesClassName(service)),
            true,
            null,
            null);
    Statement credentialProvider =
        ComposerUtils.createMemberVarStatement(
            "credentialsProvider", STATIC_TYPES.get("CredentialsProvider"), true, null, null);

    Statement loggerStatement =
        LoggerUtils.getLoggerDeclarationExpr(
            Utils.getServiceAutoConfigurationClassName(service), types);
    return Arrays.asList(clientPropertiesStatement, credentialProvider, loggerStatement);
  }

  private static MethodDefinition createConstructor(
      Service service, String className, Map<String, TypeNode> types, Expr thisExpr) {
    VariableExpr clientPropertiesVarExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setName("clientProperties")
                .setType(types.get(Utils.getServicePropertiesClassName(service)))
                .build());
    VariableExpr credentialsProviderVarExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setName("credentialsProvider")
                .setType(STATIC_TYPES.get("CredentialsProvider"))
                .build());

    // Assign this.clientProperties
    AssignmentExpr thisClientPropertiesAssignmentExpr =
        AssignmentExpr.builder()
            .setVariableExpr(
                clientPropertiesVarExpr.toBuilder().setExprReferenceExpr(thisExpr).build())
            .setValueExpr(clientPropertiesVarExpr)
            .build();
    ExprStatement thisClientPropertiesAssignmentStatement =
        ExprStatement.withExpr(thisClientPropertiesAssignmentExpr);

    // If credentials configured through properties, create DefaultCredentialsProvider from properties
    // Otherwise use credentialsProvider through constructor injection
    VariableExpr thisClientProperties =
        clientPropertiesVarExpr.toBuilder().setExprReferenceExpr(thisExpr).build();
    AssignmentExpr.Builder thisCredentialsProviderAssignmentExprBuilder =
        AssignmentExpr.builder()
            .setVariableExpr(
                credentialsProviderVarExpr.toBuilder().setExprReferenceExpr(thisExpr).build());
    ExprStatement thisCredentialsProviderToGlobalAssignmentStatement =
        ExprStatement.withExpr(
            thisCredentialsProviderAssignmentExprBuilder
                .setValueExpr(credentialsProviderVarExpr)
                .build());

    CastExpr newCredentialsProviderExpr =
        CastExpr.builder()
            .setExpr(
                NewObjectExpr.builder()
                    .setType(STATIC_TYPES.get("DefaultCredentialsProvider"))
                    .setArguments(thisClientProperties)
                    .build())
            .setType(STATIC_TYPES.get("CredentialsProvider"))
            .build();
    ExprStatement thisCredentialsProviderAssignmentExprNewStatement =
        ExprStatement.withExpr(
            thisCredentialsProviderAssignmentExprBuilder
                .setValueExpr(newCredentialsProviderExpr)
                .build());

    Expr clientPropertiesGetCredentials =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(thisClientProperties)
            .setMethodName("getCredentials")
            .setReturnType(STATIC_TYPES.get("Credentials"))
            .build();
    Expr clientPropertiesCredentialsHasKey =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(clientPropertiesGetCredentials)
            .setMethodName("hasKey")
            .setReturnType(TypeNode.BOOLEAN)
            .build();

    Statement logClientCredentials =
        LoggerUtils.createLoggerStatement(
            ValueExpr.withValue(
                StringObjectValue.withValue(
                    "Using credentials from " + service.name() + "-specific configuration")),
            types);
    IfStatement thisCredentialsProviderAssignmentStatement =
        createIfStatement(
            clientPropertiesCredentialsHasKey,
            Arrays.asList(logClientCredentials, thisCredentialsProviderAssignmentExprNewStatement),
            Arrays.asList(thisCredentialsProviderToGlobalAssignmentStatement));

    return MethodDefinition.constructorBuilder()
        .setScope(ScopeNode.PROTECTED)
        .setThrowsExceptions(Arrays.asList(TypeNode.withExceptionClazz(IOException.class)))
        .setReturnType(types.get(className))
        .setArguments(
            Arrays.asList(
                clientPropertiesVarExpr.toBuilder().setIsDecl(true).build(),
                credentialsProviderVarExpr.toBuilder().setIsDecl(true).build()))
        .setBody(
            Arrays.asList(
                thisClientPropertiesAssignmentStatement,
                thisCredentialsProviderAssignmentStatement))
        .build();
  }

  private static List<AnnotationNode> createClassAnnotations(
      Service service, Map<String, TypeNode> types) {
    // Generates: @ConditionalOnProperty(value = <service-prefix>.enabled", matchIfMissing = true)
    AssignmentExpr valueStringAssignmentExpr =
        AssignmentExpr.builder()
            .setVariableExpr(
                VariableExpr.withVariable(
                    Variable.builder().setName("value").setType(TypeNode.STRING).build()))
            .setValueExpr(
                ValueExpr.withValue(
                    StringObjectValue.withValue(
                        Utils.getSpringPropertyPrefix(service.pakkage(), service.name())
                            + ".enabled")))
            .build();
    AssignmentExpr matchIfMissingAssignmentExpr =
        AssignmentExpr.builder()
            .setVariableExpr(
                VariableExpr.withVariable(
                    Variable.builder().setName("matchIfMissing").setType(TypeNode.BOOLEAN).build()))
            .setValueExpr(
                ValueExpr.withValue(
                    PrimitiveValue.builder().setValue("true").setType(TypeNode.BOOLEAN).build()))
            .build();
    AnnotationNode conditionalOnPropertyNode =
        AnnotationNode.builder()
            .setType(STATIC_TYPES.get("ConditionalOnProperty"))
            .addDescription(valueStringAssignmentExpr)
            .addDescription(matchIfMissingAssignmentExpr)
            .build();

    // Generates: @ConditionalOnClass(<Service>Client.class)
    AnnotationNode conditionalOnClassNode =
        AnnotationNode.builder()
            .setType(STATIC_TYPES.get("ConditionalOnClass"))
            .setDescription(
                VariableExpr.builder()
                    .setVariable(
                        Variable.builder().setType(TypeNode.CLASS_OBJECT).setName("class").build())
                    .setStaticReferenceType(types.get("ServiceClient"))
                    .build())
            .build();

    // Generates: @AutoConfigureAfter(GcpContextAutoConfiguration.class)
    AnnotationNode autoConfigureAfterNode =
        AnnotationNode.builder()
            .setType(STATIC_TYPES.get("AutoConfigureAfter"))
            .setDescription(
                VariableExpr.builder()
                    .setVariable(
                        Variable.builder().setType(TypeNode.CLASS_OBJECT).setName("class").build())
                    .setStaticReferenceType(STATIC_TYPES.get("GcpContextAutoConfiguration"))
                    .build())
            .build();

    // Generates: @AutoConfiguration
    AnnotationNode configurationNode =
        AnnotationNode.builder().setType(STATIC_TYPES.get("AutoConfiguration")).build();

    // Generates: @EnableConfigurationProperties(<Service>SpringProperties.class)
    AnnotationNode enableConfigurationPropertiesNode =
        AnnotationNode.builder()
            .setType(STATIC_TYPES.get("EnableConfigurationProperties"))
            .setDescription(
                VariableExpr.builder()
                    .setVariable(
                        Variable.builder().setType(TypeNode.CLASS_OBJECT).setName("class").build())
                    .setStaticReferenceType(types.get(Utils.getServicePropertiesClassName(service)))
                    .build())
            .build();

    return Arrays.asList(
        configurationNode,
        autoConfigureAfterNode,
        conditionalOnClassNode,
        conditionalOnPropertyNode,
        enableConfigurationPropertiesNode);
  }

  private static MethodDefinition createTransportChannelProviderBeanMethod(
      Service service,
      Transport transport,
      String methodName,
      Map<String, TypeNode> types,
      Expr thisExpr) {
    AssignmentExpr nameStringAssignmentExpr =
        AssignmentExpr.builder()
            .setVariableExpr(
                VariableExpr.withVariable(
                    Variable.builder().setName("name").setType(TypeNode.STRING).build()))
            .setValueExpr(ValueExpr.withValue(StringObjectValue.withValue(methodName)))
            .build();
    AnnotationNode conditionalOnMissingBean =
        AnnotationNode.builder()
            .setType(STATIC_TYPES.get("ConditionalOnMissingBean"))
            .addDescription(nameStringAssignmentExpr)
            .build();

    MethodDefinition.Builder beanMethodBuilder =
        MethodDefinition.builder()
            .setHeaderCommentStatements(
                SpringAutoconfigCommentComposer.createTransportChannelProviderComment())
            .setName(methodName)
            .setScope(ScopeNode.PUBLIC)
            .setReturnType(STATIC_TYPES.get("TransportChannelProvider"))
            .setAnnotations(
                Arrays.asList(
                    AnnotationNode.withType(STATIC_TYPES.get("Bean")), conditionalOnMissingBean));

    // For GRPC-only or REST-only libraries, generates code to use default provider:
    // <Service>Settings.defaultTransportChannelProvider()
    MethodInvocationExpr defaultTransportChannelProviderExpr =
        MethodInvocationExpr.builder()
            .setMethodName("defaultTransportChannelProvider")
            .setStaticReferenceType(types.get("ServiceSettings"))
            .setReturnType(STATIC_TYPES.get("TransportChannelProvider"))
            .build();

    // For GRPC+REST libraries, generates code to choose provider according to configuration property
    if (ComposerUtils.shouldSupportRestOptionWithGrpcDefault(transport, service)) {
      Variable clientPropertiesVar =
          Variable.builder()
              .setName("clientProperties")
              .setType(types.get(Utils.getServicePropertiesClassName(service)))
              .build();
      VariableExpr thisClientPropertiesVarExpr =
          VariableExpr.withVariable(clientPropertiesVar)
              .toBuilder()
              .setExprReferenceExpr(thisExpr)
              .build();

      MethodInvocationExpr getUseRest =
          MethodInvocationExpr.builder()
              .setMethodName("getUseRest")
              .setReturnType(TypeNode.BOOLEAN)
              .setExprReferenceExpr(thisClientPropertiesVarExpr)
              .build();

      // If useRest property is true, generates code to use HTTP/JSON transport provider:
      // <Service>Settings.defaultHttpJsonTransportProviderBuilder().build()
      Expr defaultTransportProviderExprChain =
          MethodInvocationExpr.builder()
              .setStaticReferenceType(types.get("ServiceSettings"))
              .setMethodName("defaultHttpJsonTransportProviderBuilder")
              .build();
      MethodInvocationExpr defaultHttpJsonTransportProviderExpr =
          MethodInvocationExpr.builder()
              .setExprReferenceExpr(defaultTransportProviderExprChain)
              .setMethodName("build")
              .setReturnType(STATIC_TYPES.get("InstantiatingHttpJsonChannelProvider"))
              .build();

      IfStatement returnHttpJsonTransportChannelProviderStatement =
          createIfStatement(
              getUseRest,
              Arrays.asList(
                  ExprStatement.withExpr(
                      ReturnExpr.withExpr(defaultHttpJsonTransportProviderExpr))),
              null);

      return beanMethodBuilder
          .setBody(Arrays.asList(returnHttpJsonTransportChannelProviderStatement))
          .setReturnExpr(defaultTransportChannelProviderExpr)
          .build();
    }

    return beanMethodBuilder.setReturnExpr(defaultTransportChannelProviderExpr).build();
  }

  private static IfStatement createIfStatement(
      Expr conditionExpr, List<Statement> ifBody, List<Statement> elseBody) {
    IfStatement.Builder credentialIfStatement =
        IfStatement.builder().setConditionExpr(conditionExpr).setBody(ifBody);
    if (elseBody != null) {
      credentialIfStatement.setElseBody(elseBody);
    }
    return credentialIfStatement.build();
  }

  private static MethodDefinition createSettingsBeanMethod(
      Service service,
      Transport transport,
      String transportChannelProviderName,
      Map<String, TypeNode> types,
      Expr thisExpr,
      String serviceSettingsMethodName) {

    // Generates code for argument variables:
    VariableExpr credentialsProviderVariableExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setName("credentialsProvider")
                .setType(STATIC_TYPES.get("CredentialsProvider"))
                .build());
    VariableExpr transportChannelProviderVariableExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setName("defaultTransportChannelProvider")
                .setType(STATIC_TYPES.get("TransportChannelProvider"))
                .build());

    List<Statement> bodyStatements = new ArrayList<>();

    Variable settingBuilderVariable =
        Variable.builder()
            .setName("clientSettingsBuilder")
            .setType(types.get("ServiceSettingsBuilder"))
            .build();

    VariableExpr settingsVarExpr =
        VariableExpr.withVariable(settingBuilderVariable).toBuilder().setIsDecl(true).build();

    Expr newBuilderExpr =
        MethodInvocationExpr.builder()
            .setStaticReferenceType(types.get("ServiceSettings"))
            .setMethodName("newBuilder")
            .setReturnType(types.get("ServiceSettingsBuilder"))
            .build();

    Variable clientPropertiesVar =
        Variable.builder()
            .setName("clientProperties")
            .setType(types.get(Utils.getServicePropertiesClassName(service)))
            .build();
    VariableExpr thisClientPropertiesVarExpr =
        VariableExpr.withVariable(clientPropertiesVar)
            .toBuilder()
            .setExprReferenceExpr(thisExpr)
            .build();

    if (ComposerUtils.shouldSupportRestOptionWithGrpcDefault(transport, service)) {
      // For GRPC+REST libraries, generates code to choose builder according to configuration property
      MethodInvocationExpr getUseRest =
          MethodInvocationExpr.builder()
              .setMethodName("getUseRest")
              .setReturnType(TypeNode.BOOLEAN)
              .setExprReferenceExpr(thisClientPropertiesVarExpr)
              .build();

      // If useRest property is true, generates code to use HTTP/JSON builder:
      // <Service>Settings.newHttpJsonBuilder()
      Expr newHttpJsonBuilderExpr =
          MethodInvocationExpr.builder()
              .setStaticReferenceType(types.get("ServiceSettings"))
              .setMethodName("newHttpJsonBuilder")
              .setReturnType(types.get("ServiceSettingsBuilder"))
              .build();

      AssignmentExpr newHttpJsonBuilderAssignmentExpr =
          AssignmentExpr.builder()
              .setVariableExpr(VariableExpr.withVariable(settingBuilderVariable))
              .setValueExpr(newHttpJsonBuilderExpr)
              .build();

      AssignmentExpr newBuilderAssignmentExpr =
          AssignmentExpr.builder()
              .setVariableExpr(VariableExpr.withVariable(settingBuilderVariable))
              .setValueExpr(newBuilderExpr)
              .build();

      ExprStatement newBuilderStatement = ExprStatement.withExpr(newBuilderAssignmentExpr);
      ExprStatement newHttpJsonBuilderStatement =
          ExprStatement.withExpr(newHttpJsonBuilderAssignmentExpr);

      IfStatement setClientSettingsBuilderStatement =
          createIfStatement(
              getUseRest,
              Arrays.asList(
                  newHttpJsonBuilderStatement,
                  LoggerUtils.createLoggerStatement(
                      ValueExpr.withValue(
                          StringObjectValue.withValue("Using REST (HTTP/JSON) transport.")),
                      types)),
              Arrays.asList(newBuilderStatement));

      bodyStatements.add(ExprStatement.withExpr(settingsVarExpr));
      bodyStatements.add(setClientSettingsBuilderStatement);

    } else {
      // For GRPC-only or REST-only libraries, generates code to use default builder:
      // <Service>Settings.newBuilder()
      AssignmentExpr clientSettingsBuilderAssignmentExpr =
          AssignmentExpr.builder()
              .setVariableExpr(settingsVarExpr)
              .setValueExpr(newBuilderExpr)
              .build();

      bodyStatements.add(ExprStatement.withExpr(clientSettingsBuilderAssignmentExpr));
    }

    VariableExpr thisCredentialsProvider =
        VariableExpr.withVariable(
                Variable.builder()
                    .setName("credentialsProvider")
                    .setType(STATIC_TYPES.get("CredentialsProvider"))
                    .build())
            .toBuilder()
            .setExprReferenceExpr(thisExpr)
            .build();

    Expr settingsBuilderExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(settingBuilderVariable))
            .setMethodName("setCredentialsProvider")
            .setArguments(thisCredentialsProvider)
            .build();
    settingsBuilderExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(settingsBuilderExpr)
            .setMethodName("setTransportChannelProvider")
            .setArguments(transportChannelProviderVariableExpr)
            .build();
    MethodInvocationExpr userAgentHeaderProviderInvocation =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(thisExpr)
            .setMethodName("userAgentHeaderProvider")
            .setReturnType(STATIC_TYPES.get("HeaderProvider"))
            .build();
    settingsBuilderExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(settingsBuilderExpr)
            .setMethodName("setHeaderProvider")
            .setArguments(userAgentHeaderProviderInvocation)
            .setReturnType(settingBuilderVariable.type())
            .build();

    bodyStatements.add(ExprStatement.withExpr(settingsBuilderExpr));

    // Set quota project ID if specified via configuration property
    MethodInvocationExpr getQuotaProjectId =
        MethodInvocationExpr.builder()
            .setMethodName("getQuotaProjectId")
            .setReturnType(TypeNode.STRING)
            .setExprReferenceExpr(thisClientPropertiesVarExpr)
            .build();
    RelationalOperationExpr projectIdIsNull =
        RelationalOperationExpr.notEqualToWithExprs(getQuotaProjectId, ValueExpr.createNullExpr());

    MethodInvocationExpr setQuotaProjectId =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(settingBuilderVariable))
            .setMethodName("setQuotaProjectId")
            .setArguments(getQuotaProjectId)
            .build();

    Statement projectIdLoggerStatement =
        LoggerUtils.createLoggerStatement(
            LoggerUtils.concatManyWithExprs(
                ValueExpr.withValue(StringObjectValue.withValue("Quota project id set to ")),
                getQuotaProjectId,
                ValueExpr.withValue(
                    StringObjectValue.withValue(", this overrides project id from credentials."))),
            types);

    IfStatement setQuotaProjectIdStatement =
        createIfStatement(
            projectIdIsNull,
            Arrays.asList(ExprStatement.withExpr(setQuotaProjectId), projectIdLoggerStatement),
            null);

    bodyStatements.add(setQuotaProjectIdStatement);

    // Set executor thread count if specified via configuration property
    MethodInvocationExpr getExecutorThreadCount =
        MethodInvocationExpr.builder()
            .setMethodName("getExecutorThreadCount")
            .setReturnType(TypeNode.INT_OBJECT)
            .setExprReferenceExpr(thisClientPropertiesVarExpr)
            .build();
    RelationalOperationExpr executorThreadCountIsNull =
        RelationalOperationExpr.notEqualToWithExprs(
            getExecutorThreadCount, ValueExpr.createNullExpr());

    VariableExpr executorProviderVarExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setType(STATIC_TYPES.get("ExecutorProvider"))
                .setName("executorProvider")
                .build());

    MethodInvocationExpr chainedMethodToSetExecutorProvider =
        MethodInvocationExpr.builder()
            .setStaticReferenceType(types.get("ServiceSettings"))
            .setMethodName("defaultExecutorProviderBuilder")
            .build();
    chainedMethodToSetExecutorProvider =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(chainedMethodToSetExecutorProvider)
            .setMethodName("setExecutorThreadCount")
            .setArguments(getExecutorThreadCount)
            .build();
    chainedMethodToSetExecutorProvider =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(chainedMethodToSetExecutorProvider)
            .setMethodName("build")
            .setReturnType(STATIC_TYPES.get("ExecutorProvider"))
            .build();
    AssignmentExpr executorProviderAssignExpr =
        AssignmentExpr.builder()
            .setVariableExpr(executorProviderVarExpr.toBuilder().setIsDecl(true).build())
            .setValueExpr(chainedMethodToSetExecutorProvider)
            .build();
    MethodInvocationExpr setBackgroundExecutorProvider =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(settingBuilderVariable))
            .setMethodName("setBackgroundExecutorProvider")
            .setArguments(executorProviderVarExpr)
            .build();

    Statement backgroundExecutorLoggerStatement =
        LoggerUtils.createLoggerStatement(
            ArithmeticOperationExpr.concatWithExprs(
                ValueExpr.withValue(
                    StringObjectValue.withValue("Background executor thread count is ")),
                getExecutorThreadCount),
            types);
    IfStatement setBackgroundExecutorProviderStatement =
        createIfStatement(
            executorThreadCountIsNull,
            Arrays.asList(
                ExprStatement.withExpr(executorProviderAssignExpr),
                ExprStatement.withExpr(setBackgroundExecutorProvider),
                backgroundExecutorLoggerStatement),
            null);

    bodyStatements.add(setBackgroundExecutorProviderStatement);

    // If service-level properties configured, update retry settings for each method
    Variable serviceRetryPropertiesVar =
        Variable.builder().setName("serviceRetry").setType(types.get("Retry")).build();

    VariableExpr serviceRetryPropertiesVarExpr =
        VariableExpr.builder().setVariable(serviceRetryPropertiesVar).setIsDecl(true).build();

    // Service-level retry configuration: clientProperties.getRetry()
    MethodInvocationExpr serviceRetryPropertiesExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(clientPropertiesVar))
            .setMethodName("getRetry")
            .setReturnType(types.get("Retry"))
            .build();

    AssignmentExpr serviceRetrySettingsExpr =
        AssignmentExpr.builder()
            .setVariableExpr(serviceRetryPropertiesVarExpr)
            .setValueExpr(serviceRetryPropertiesExpr)
            .build();

    bodyStatements.add(ExprStatement.withExpr(serviceRetrySettingsExpr));

    RelationalOperationExpr serviceRetryPropertiesNotNull =
        RelationalOperationExpr.notEqualToWithExprs(
            VariableExpr.withVariable(serviceRetryPropertiesVar), ValueExpr.createNullExpr());

    List<Statement> updateRetrySettingsStatementBody = new ArrayList<>();

    for (Method method : Utils.getMethodsForRetryConfiguration(service)) {
      List<Statement> updateMethodWithServiceRetryStatements =
          createUpdateRetrySettingsStatements(
              method.name(), settingBuilderVariable, serviceRetryPropertiesVar, types);
      updateRetrySettingsStatementBody.addAll(updateMethodWithServiceRetryStatements);
      updateRetrySettingsStatementBody.add(EMPTY_LINE_STATEMENT);
    }

    updateRetrySettingsStatementBody.add(
        LoggerUtils.createLoggerStatement(
            ValueExpr.withValue(
                StringObjectValue.withValue(
                    "Configured service-level retry settings from properties.")),
            types));

    IfStatement setRetrySettingsStatement =
        createIfStatement(serviceRetryPropertiesNotNull, updateRetrySettingsStatementBody, null);

    bodyStatements.add(setRetrySettingsStatement);

    // If-blocks to update with method-level properties
    for (Method method : Utils.getMethodsForRetryConfiguration(service)) {
      String methodNameLowerCamel = JavaStyle.toLowerCamelCase(method.name());
      String methodNameUpperCamel = JavaStyle.toUpperCamelCase(method.name());

      Variable methodRetryPropertiesVar =
          Variable.builder()
              .setName(methodNameLowerCamel + "Retry")
              .setType(types.get("Retry"))
              .build();

      VariableExpr methodRetryPropertiesVarExpr =
          VariableExpr.builder().setVariable(methodRetryPropertiesVar).setIsDecl(true).build();

      // Method-level retry configuration: clientProperties.get<Method>Retry()
      MethodInvocationExpr methodRetryPropertiesExpr =
          MethodInvocationExpr.builder()
              .setExprReferenceExpr(VariableExpr.withVariable(clientPropertiesVar))
              .setMethodName(String.format("get%sRetry", methodNameUpperCamel))
              .setReturnType(types.get("Retry"))
              .build();

      AssignmentExpr methodRetrySettingsExpr =
          AssignmentExpr.builder()
              .setVariableExpr(methodRetryPropertiesVarExpr)
              .setValueExpr(methodRetryPropertiesExpr)
              .build();

      bodyStatements.add(ExprStatement.withExpr(methodRetrySettingsExpr));

      RelationalOperationExpr methodRetryPropertiesNotNull =
          RelationalOperationExpr.notEqualToWithExprs(
              VariableExpr.withVariable(methodRetryPropertiesVar), ValueExpr.createNullExpr());

      List<Statement> updateMethodRetrySettingsStatementBody =
          createUpdateRetrySettingsStatements(
              method.name(), settingBuilderVariable, methodRetryPropertiesVar, types);

      updateMethodRetrySettingsStatementBody.add(
          LoggerUtils.createLoggerStatement(
              ValueExpr.withValue(
                  StringObjectValue.withValue(
                      String.format(
                          "Configured method-level retry settings for %s from properties.",
                          methodNameLowerCamel))),
              types));

      IfStatement setMethodRetrySettingsStatement =
          createIfStatement(
              methodRetryPropertiesNotNull, updateMethodRetrySettingsStatementBody, null);

      bodyStatements.add(setMethodRetrySettingsStatement);
    }

    // return expressions
    MethodInvocationExpr returnExpr =
        MethodInvocationExpr.builder()
            .setMethodName("build")
            .setExprReferenceExpr(settingsVarExpr.toBuilder().setIsDecl(false).build())
            .setReturnType(types.get("ServiceSettings"))
            .build();
    List<VariableExpr> argumentsVariableExprs =
        Arrays.asList(
            transportChannelProviderVariableExpr
                .toBuilder()
                .setIsDecl(true)
                .setAnnotations(
                    Arrays.asList(
                        AnnotationNode.builder()
                            .setType(STATIC_TYPES.get("Qualifier"))
                            .setDescription(transportChannelProviderName)
                            .build()))
                .build());

    return MethodDefinition.builder()
        .setHeaderCommentStatements(
            SpringAutoconfigCommentComposer.createSettingsBeanComment(
                service,
                Utils.getServicePropertiesClassName(service),
                transportChannelProviderName))
        .setName(serviceSettingsMethodName)
        .setScope(ScopeNode.PUBLIC)
        .setReturnType(types.get("ServiceSettings"))
        .setArguments(argumentsVariableExprs)
        .setAnnotations(
            Arrays.asList(
                AnnotationNode.withType(STATIC_TYPES.get("Bean")),
                AnnotationNode.withType(STATIC_TYPES.get("ConditionalOnMissingBean"))))
        .setThrowsExceptions(Arrays.asList(TypeNode.withExceptionClazz(IOException.class)))
        .setReturnExpr(returnExpr)
        .setBody(bodyStatements)
        .build();
  }

  private static List<Statement> createUpdateRetrySettingsStatements(
      String methodName,
      Variable settingBuilderVariable,
      Variable retryFromPropertiesVar,
      Map<String, TypeNode> types) {

    List<Statement> results = new ArrayList<>();
    String methodNameLowerCamel = JavaStyle.toLowerCamelCase(methodName);
    String settingsVarName = methodNameLowerCamel + "Settings";
    String retrySettingsVarName = methodNameLowerCamel + "RetrySettings";

    Variable methodRetrySettingsVariable =
        Variable.builder()
            .setName(retrySettingsVarName)
            .setType(STATIC_TYPES.get("RetrySettings"))
            .build();

    MethodInvocationExpr methodSettingsExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(VariableExpr.withVariable(settingBuilderVariable))
            .setMethodName(settingsVarName)
            .build();

    // Generates code to get retry settings (client library defaults):
    // clientSettingsBuilder.analyzeSentimentSettings().getRetrySettings()
    MethodInvocationExpr getRetrySettingsExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(methodSettingsExpr)
            .setMethodName(String.format("getRetrySettings"))
            .build();

    MethodInvocationExpr updatedRetrySettingsExpr =
        MethodInvocationExpr.builder()
            .setStaticReferenceType(types.get("RetryUtil"))
            .setMethodName("updateRetrySettings")
            .setArguments(
                Arrays.asList(
                    getRetrySettingsExpr, VariableExpr.withVariable(retryFromPropertiesVar)))
            .setReturnType(STATIC_TYPES.get("RetrySettings"))
            .build();

    // Generates code to set retry settings:
    // clientSettingsBuilder.analyzeSentimentSettings().setRetrySettings(analyzeSentimentRetrySettings)
    MethodInvocationExpr setRetrySettingsExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(methodSettingsExpr)
            .setMethodName(String.format("setRetrySettings"))
            .setArguments(VariableExpr.withVariable(methodRetrySettingsVariable))
            .build();

    results.add(
        ExprStatement.withExpr(
            AssignmentExpr.builder()
                .setVariableExpr(
                    VariableExpr.builder()
                        .setVariable(methodRetrySettingsVariable)
                        .setIsDecl(true)
                        .build())
                .setValueExpr(updatedRetrySettingsExpr)
                .build()));
    results.add(ExprStatement.withExpr(setRetrySettingsExpr));

    return results;
  }

  private static MethodDefinition createClientBeanMethod(
      Map<String, TypeNode> types, Service service, String serviceSettingsMethodName) {
    VariableExpr clientSettingsVariableExpr =
        VariableExpr.withVariable(
            Variable.builder()
                .setName(serviceSettingsMethodName)
                .setType(types.get("ServiceSettings"))
                .build());
    MethodInvocationExpr returnExpr =
        MethodInvocationExpr.builder()
            .setMethodName("create")
            .setStaticReferenceType(types.get("ServiceClient"))
            .setReturnType(types.get("ServiceClient"))
            .setArguments(clientSettingsVariableExpr)
            .build();
    List<VariableExpr> argumentsVariableExprs =
        Arrays.asList(clientSettingsVariableExpr.toBuilder().setIsDecl(true).build());
    String methodName = JavaStyle.toLowerCamelCase(service.name()) + "Client";
    return MethodDefinition.builder()
        .setHeaderCommentStatements(
            SpringAutoconfigCommentComposer.createClientBeanComment(
                service, serviceSettingsMethodName))
        .setName(methodName)
        .setScope(ScopeNode.PUBLIC)
        .setReturnType(types.get("ServiceClient"))
        .setArguments(argumentsVariableExprs)
        .setAnnotations(
            Arrays.asList(
                AnnotationNode.withType(STATIC_TYPES.get("Bean")),
                AnnotationNode.withType(STATIC_TYPES.get("ConditionalOnMissingBean"))))
        .setThrowsExceptions(Arrays.asList(TypeNode.withExceptionClazz(IOException.class)))
        .setReturnExpr(returnExpr)
        .build();
  }

  private static MethodDefinition createUserAgentHeaderProviderMethod(
      String serviceName, String className, Map<String, TypeNode> types, Expr thisExpr) {
    // Generates method definition that returns a spring-specific HeaderProvider,
    // with user-agent header set for metrics
    List<Statement> bodyStatements = new ArrayList<>();

    VariableExpr springLibStringVariableExpr =
        VariableExpr.builder()
            .setVariable(
                Variable.builder().setName("springLibrary").setType(TypeNode.STRING).build())
            .setIsDecl(true)
            .build();
    Expr springLibStringValueExpr =
        ValueExpr.withValue(StringObjectValue.withValue("spring-autogen-" + serviceName));

    AssignmentExpr springLibStringAssignExpr =
        AssignmentExpr.builder()
            .setVariableExpr(springLibStringVariableExpr)
            .setValueExpr(springLibStringValueExpr)
            .build();
    bodyStatements.add(ExprStatement.withExpr(springLibStringAssignExpr));

    VariableExpr versionStringVariableExpr =
        VariableExpr.builder()
            .setVariable(Variable.builder().setName("version").setType(TypeNode.STRING).build())
            .setIsDecl(true)
            .build();
    Expr thisVersionExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(thisExpr)
            .setMethodName("getClass")
            .build();
    thisVersionExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(thisVersionExpr)
            .setMethodName("getPackage")
            .build();
    thisVersionExpr =
        MethodInvocationExpr.builder()
            .setExprReferenceExpr(thisVersionExpr)
            .setMethodName("getImplementationVersion")
            .setReturnType(TypeNode.STRING)
            .build();
    AssignmentExpr versionStringAssignExpr =
        AssignmentExpr.builder()
            .setVariableExpr(versionStringVariableExpr)
            .setValueExpr(thisVersionExpr)
            .build();
    bodyStatements.add(ExprStatement.withExpr(versionStringAssignExpr));

    ValueExpr slash = ValueExpr.withValue(StringObjectValue.withValue("/"));
    ArithmeticOperationExpr userAgentStringConcat =
        ArithmeticOperationExpr.concatWithExprs(
            springLibStringVariableExpr.toBuilder().setIsDecl(false).build(), slash);
    userAgentStringConcat =
        ArithmeticOperationExpr.concatWithExprs(
            userAgentStringConcat, versionStringVariableExpr.toBuilder().setIsDecl(false).build());
    Expr collectionsExpr =
        MethodInvocationExpr.builder()
            .setStaticReferenceType(STATIC_TYPES.get("Collections"))
            .setMethodName("singletonMap")
            .setArguments(
                ValueExpr.withValue(StringObjectValue.withValue("user-agent")),
                userAgentStringConcat)
            .setReturnType(STATIC_TYPES.get("HeaderProvider"))
            .build();
    LambdaExpr returnExpr = LambdaExpr.builder().setReturnExpr(collectionsExpr).build();
    return MethodDefinition.builder()
        .setName("userAgentHeaderProvider")
        .setScope(ScopeNode.PRIVATE)
        .setReturnType(STATIC_TYPES.get("HeaderProvider"))
        .setReturnExpr(returnExpr)
        .setBody(bodyStatements)
        .build();
  }

  private static Map<String, TypeNode> createStaticTypes() {
    List<Class<?>> concreteClazzes =
        Arrays.asList(
            TransportChannelProvider.class,
            InstantiatingHttpJsonChannelProvider.class,
            ExecutorProvider.class,
            ConditionalOnClass.class,
            ConditionalOnProperty.class,
            ConditionalOnMissingBean.class,
            EnableConfigurationProperties.class,
            CredentialsProvider.class,
            GcpContextAutoConfiguration.class,
            AutoConfiguration.class,
            AutoConfigureAfter.class,
            Bean.class,
            Qualifier.class,
            DefaultCredentialsProvider.class,
            RetrySettings.class,
            HeaderProvider.class,
            Collections.class,
            Credentials.class);
    Map<String, TypeNode> concreteClazzesMap =
        concreteClazzes.stream()
            .collect(
                Collectors.toMap(
                    Class::getSimpleName,
                    c -> TypeNode.withReference(ConcreteReference.withClazz(c))));
    return concreteClazzesMap;
  }

  private static Map<String, TypeNode> createDynamicTypes(Service service, String packageName) {
    Map<String, TypeNode> typeMap = new HashMap<>();
    TypeNode clientAutoconfiguration =
        TypeNode.withReference(
            VaporReference.builder()
                .setName(Utils.getServiceAutoConfigurationClassName(service))
                .setPakkage(packageName)
                .build());

    TypeNode clientProperties =
        TypeNode.withReference(
            VaporReference.builder()
                .setName(Utils.getServicePropertiesClassName(service))
                .setPakkage(packageName)
                .build());

    // TODO: This should move to static types once class is added into spring-cloud-gcp-core
    TypeNode retryProperties =
        TypeNode.withReference(
            VaporReference.builder()
                .setName("Retry")
                .setPakkage("com.google.cloud.spring.core")
                .build());

    // TODO: This should move to static types once class is added into spring-cloud-gcp-core
    TypeNode retryUtil =
        TypeNode.withReference(
            VaporReference.builder()
                .setName("RetryUtil")
                .setPakkage("com.google.cloud.spring.core.util")
                .build());

    TypeNode serviceClient =
        TypeNode.withReference(
            VaporReference.builder()
                .setName(ClassNames.getServiceClientClassName(service))
                .setPakkage(service.pakkage())
                .build());
    TypeNode serviceSettings =
        TypeNode.withReference(
            VaporReference.builder()
                .setName(ClassNames.getServiceSettingsClassName(service))
                .setPakkage(service.pakkage())
                .build());
    TypeNode serviceSettingsBuilder =
        TypeNode.withReference(
            VaporReference.builder()
                .setPakkage(service.pakkage())
                .setName("Builder")
                .setEnclosingClassNames(ClassNames.getServiceSettingsClassName(service))
                .build());

    typeMap.put(Utils.getServiceAutoConfigurationClassName(service), clientAutoconfiguration);
    typeMap.put(Utils.getServicePropertiesClassName(service), clientProperties);
    typeMap.put("ServiceClient", serviceClient);
    typeMap.put("ServiceSettings", serviceSettings);
    typeMap.put("ServiceSettingsBuilder", serviceSettingsBuilder);
    typeMap.put("Retry", retryProperties);
    typeMap.put("RetryUtil", retryUtil);

    return typeMap;
  }
}

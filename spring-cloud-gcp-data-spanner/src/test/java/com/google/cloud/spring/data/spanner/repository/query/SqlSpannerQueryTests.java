/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.data.spanner.repository.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Value;
import com.google.cloud.spring.data.spanner.core.SpannerMutationFactory;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerReadConverter;
import com.google.cloud.spring.data.spanner.core.convert.SpannerWriteConverter;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.cloud.spring.data.spanner.core.mapping.Where;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.spanner.v1.TypeCode;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.expression.ValueEvaluationContext;
import org.springframework.data.expression.ValueEvaluationContextProvider;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersSource;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.QueryMethodValueEvaluationContextAccessor;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/** Tests Spanner SQL Query Methods. */
class SqlSpannerQueryTests {

  private static final Offset<Double> DELTA = Offset.offset(0.00001);

  private SpannerTemplate spannerTemplate;

  private SpannerQueryMethod queryMethod;

  private ValueExpressionDelegate valueExpressionDelegate;

  private QueryMethodEvaluationContextProvider evaluationContextProvider;

  private SpelExpressionParser expressionParser;

  private SpannerMappingContext spannerMappingContext = new SpannerMappingContext(new Gson());

  private ValueEvaluationContext valueEvaluationContext;

  private final Sort sort = Sort.by(Order.asc("COLA"), Order.desc("COLB"));

  private final Pageable pageable = PageRequest.of(3, 10, this.sort);

  private final SpannerEntityProcessor spannerEntityProcessor = mock(SpannerEntityProcessor.class);

  private final DatabaseClient databaseClient = mock(DatabaseClient.class);


  @BeforeEach
  void initMocks() throws NoSuchMethodException {
    this.queryMethod = mock(SpannerQueryMethod.class);
    // this is a dummy object. it is not mockable otherwise.
    Method method = Object.class.getMethod("toString");
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    when(this.spannerEntityProcessor.getWriteConverter()).thenReturn(new SpannerWriteConverter());
    when(this.spannerEntityProcessor.getReadConverter()).thenReturn(new SpannerReadConverter());
    this.spannerTemplate =
        spy(
            new SpannerTemplate(
                () -> this.databaseClient,
                this.spannerMappingContext,
                this.spannerEntityProcessor,
                mock(SpannerMutationFactory.class),
                new SpannerSchemaUtils(
                    this.spannerMappingContext, this.spannerEntityProcessor, true)));
    this.expressionParser = new SpelExpressionParser();
    this.evaluationContextProvider = mock(QueryMethodEvaluationContextProvider.class);

    this.valueExpressionDelegate = mock(ValueExpressionDelegate.class);
    QueryMethodValueEvaluationContextAccessor evaluationContextAccessor = mock(QueryMethodValueEvaluationContextAccessor.class);
    ValueEvaluationContextProvider evaluationContextProvider =
        mock(ValueEvaluationContextProvider.class);
    this.valueEvaluationContext = mock(ValueEvaluationContext.class);
    when(this.valueExpressionDelegate.getEvaluationContextAccessor())
        .thenReturn(evaluationContextAccessor);
    when(evaluationContextAccessor.create(any())).thenReturn(evaluationContextProvider);
    when(evaluationContextProvider.getEvaluationContext(any())).thenReturn(valueEvaluationContext);
    when(valueEvaluationContext.getEvaluationContext()).thenReturn(mock(EvaluationContext.class));

  }

  @SuppressWarnings("deprecation")
  private <T> SqlSpannerQuery<T> createQuery(String sql, Class<T> theClass, boolean isDml, boolean useValueExpressionDelegate) {
    if (useValueExpressionDelegate) {
      return new SqlSpannerQuery<T>(
          theClass,
          this.queryMethod,
          this.spannerTemplate,
          sql,
          this.valueExpressionDelegate,
          this.expressionParser,
          this.spannerMappingContext,
          isDml);
    }
    return new SqlSpannerQuery<T>(
        theClass,
        this.queryMethod,
        this.spannerTemplate,
        sql,
        this.evaluationContextProvider,
        this.expressionParser,
        this.spannerMappingContext,
        isDml);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void noPageableParamQueryTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {
    String sql =
        "SELECT DISTINCT * FROM "
            + ":com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Trade:";
    // @formatter:off
    String entityResolvedSql =
        "SELECT *, ARRAY (SELECT AS STRUCT childId, disabled, id, value, ARRAY (SELECT AS STRUCT"
            + " canceled, childId, content, documentId, id FROM documents WHERE (documents.id ="
            + " children.id AND documents.childId = children.childId) AND (canceled = false)) AS"
            + " documents FROM children WHERE (children.id = trades.id) AND (disabled = false)) AS"
            + " children FROM (SELECT DISTINCT * FROM trades) trades";
    // @formatter:on

    final Class toReturn = Trade.class;
    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn(toReturn);

    EvaluationContext evaluationContext = new StandardEvaluationContext();
    when(this.evaluationContextProvider.getEvaluationContext(any(), any()))
        .thenReturn(evaluationContext);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, toReturn, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              return null;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method method = QueryHolder.class.getMethod("dummyMethod2");
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    sqlSpannerQuery.execute(new Object[] {});

    verify(this.spannerTemplate, times(1)).executeQuery(any(), any());
    verify(this.spannerTemplate, times(1))
        .query(eq(Trade.class), any(Statement.class), any(SpannerQueryOptions.class));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void pageableParamQueryTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {

    String sql =
        "SELECT * FROM"
            + " :com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Child:"
            + " WHERE id = @id AND trader_id = @trader_id";
    // @formatter:off
    String entityResolvedSql =
        "SELECT *, ARRAY (SELECT AS STRUCT canceled, childId, content, documentId, id FROM"
            + " documents WHERE (documents.id = children.id AND documents.childId ="
            + " children.childId) AND (canceled = false)) AS documents FROM (SELECT * FROM children"
            + " WHERE id = @id AND trader_id = @trader_id) children WHERE disabled = false ORDER BY"
            + " trader_id ASC LIMIT 10 OFFSET 30";
    // @formatter:on

    Object[] params =
        new Object[] {"ID", "TRADER_ID", PageRequest.of(3, 10, Sort.by(Order.asc("trader_id")))};
    String[] paramNames = new String[] {"id", "trader_id", "ignoredPageable"};

    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn((Class) Child.class);

    EvaluationContext evaluationContext = new StandardEvaluationContext();
    for (int i = 0; i < params.length; i++) {
      evaluationContext.setVariable(paramNames[i], params[i]);
    }
    when(this.evaluationContextProvider.getEvaluationContext(any(), any()))
        .thenReturn(evaluationContext);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Child.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              Map<String, Value> paramMap = statement.getParameters();

              assertThat(paramMap.get("id").getString()).isEqualTo(params[0]);
              assertThat(paramMap.get("traderId").getString()).isEqualTo(params[1]);
              assertThat(paramMap.get("ignoredPageable")).isNull();

              return null;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method method =
        QueryHolder.class.getMethod("dummyMethod4", String.class, String.class, Pageable.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    sqlSpannerQuery.execute(params);

    verify(this.spannerTemplate, times(1)).executeQuery(any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void sortParamQueryTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {

    String sql =
        "SELECT * FROM"
            + " :com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Child:"
            + " WHERE id = @id AND trader_id = @trader_id";
    // @formatter:off
    String entityResolvedSql =
        "SELECT *, ARRAY (SELECT AS STRUCT canceled, childId, content, documentId, id FROM"
            + " documents WHERE (documents.id = children.id AND documents.childId ="
            + " children.childId) AND (canceled = false)) AS documents FROM (SELECT * FROM children"
            + " WHERE id = @id AND trader_id = @trader_id) children WHERE disabled = false ORDER BY"
            + " trader_id ASC";
    // @formatter:on

    Object[] params = new Object[] {"ID", "TRADER_ID", Sort.by(Order.asc("trader_id"))};
    String[] paramNames = new String[] {"id", "trader_id", "ignoredSort"};

    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn((Class) Child.class);

    EvaluationContext evaluationContext = new StandardEvaluationContext();
    for (int i = 0; i < params.length; i++) {
      evaluationContext.setVariable(paramNames[i], params[i]);
    }
    when(this.evaluationContextProvider.getEvaluationContext(any(), any()))
        .thenReturn(evaluationContext);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Child.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              Map<String, Value> paramMap = statement.getParameters();

              assertThat(paramMap.get("id").getString()).isEqualTo(params[0]);
              assertThat(paramMap.get("traderId").getString()).isEqualTo(params[1]);
              assertThat(paramMap.get("ignoredSort")).isNull();

              return null;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method method =
        QueryHolder.class.getMethod("dummyMethod5", String.class, String.class, Sort.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    sqlSpannerQuery.execute(params);

    verify(this.spannerTemplate, times(1)).executeQuery(any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void sortAndPageableQueryTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {

    String sql =
        "SELECT * FROM"
            + " :com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Child:"
            + " WHERE id = @id AND trader_id = @trader_id";
    // @formatter:off
    String entityResolvedSql =
        "SELECT *, ARRAY (SELECT AS STRUCT canceled, childId, content, documentId, id FROM"
            + " documents WHERE (documents.id = children.id AND documents.childId ="
            + " children.childId) AND (canceled = false)) AS documents FROM (SELECT * FROM children"
            + " WHERE id = @id AND trader_id = @trader_id) children WHERE disabled = false ORDER BY"
            + " trader_id ASC LIMIT 2 OFFSET 2";
    // @formatter:on

    Object[] params =
        new Object[] {"ID", "TRADER_ID", Sort.by(Order.asc("trader_id")), PageRequest.of(1, 2)};
    String[] paramNames = new String[] {"id", "trader_id", "ignoredSort", "pageable"};

    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn((Class) Child.class);

    EvaluationContext evaluationContext = new StandardEvaluationContext();
    for (int i = 0; i < params.length; i++) {
      evaluationContext.setVariable(paramNames[i], params[i]);
    }
    when(this.evaluationContextProvider.getEvaluationContext(any(), any()))
        .thenReturn(evaluationContext);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Child.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              Map<String, Value> paramMap = statement.getParameters();

              assertThat(paramMap.get("id").getString()).isEqualTo(params[0]);
              assertThat(paramMap.get("traderId").getString()).isEqualTo(params[1]);
              assertThat(paramMap.get("ignoredSort")).isNull();
              assertThat(paramMap.get("pageable")).isNull();

              return null;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    Method method =
        QueryHolder.class.getMethod(
            "sortAndPageable", String.class, String.class, Sort.class, Pageable.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    sqlSpannerQuery.execute(params);

    verify(this.spannerTemplate, times(1)).executeQuery(any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void compoundNameConventionTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {

    String sql =
        "SELECT DISTINCT * FROM "
            + ":com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Trade:"
            + "@{index=fakeindex}"
            + " WHERE price=#{#tag3 * -1} AND price<>#{#tag3 * -1} OR "
            + "price<>#{#tag4 * -1} AND "
            + "( action=@tag0 AND ticker=@tag1 ) OR "
            + "( trader_id=@tag2 AND price<@tag3 ) OR ( price>=@tag4 AND id IS NOT NULL AND "
            + "trader_id=NULL AND trader_id LIKE %@tag5 AND price=TRUE AND price=FALSE AND "
            + "struct_val = @tag8 AND struct_val = @tag9 "
            + "price>@tag6 AND price<=@tag7 and price in unnest(@tag10)) ORDER BY id DESC LIMIT 3;";

    // @formatter:off
    String entityResolvedSql =
        "SELECT *, ARRAY (SELECT AS STRUCT childId, disabled, id, value, ARRAY (SELECT AS STRUCT"
            + " canceled, childId, content, documentId, id FROM documents WHERE (documents.id ="
            + " children.id AND documents.childId = children.childId) AND (canceled = false)) AS"
            + " documents FROM children WHERE (children.id = trades.id) AND (disabled = false)) AS"
            + " children FROM (SELECT DISTINCT * FROM trades@{index=fakeindex} WHERE"
            + " price=@SpELtag1 AND price<>@SpELtag1 OR price<>@SpELtag2 AND ( action=@tag0 AND"
            + " ticker=@tag1 ) OR ( trader_id=@tag2 AND price<@tag3 ) OR ( price>=@tag4 AND"
            + " id IS NOT NULL AND trader_id=NULL AND trader_id LIKE %@tag5 AND price=TRUE AND"
            + " price=FALSE AND struct_val = @tag8 AND struct_val = @tag9 price>@tag6 AND"
            + " price<=@tag7 and price in unnest(@tag10)) ORDER BY id DESC LIMIT 3) trades ORDER BY"
            + " COLA ASC , COLB DESC LIMIT 10 OFFSET 30";
    // @formatter:on

    Object[] params =
        new Object[] {
          "BUY",
          this.pageable,
          "abcd",
          "abc123",
          8.88,
          3.33,
          "blahblah",
          1.11,
          2.22,
          Struct.newBuilder().set("symbol").to("ABCD").set("action").to("BUY").build(),
          new SymbolAction("ABCD", "BUY"),
          Arrays.asList("a", "b")
        };

    String[] paramNames =
        new String[] {
          "tag0",
          "ignoredPageable",
          "tag1",
          "tag2",
          "tag3",
          "tag4",
          "tag5",
          "tag6",
          "tag7",
          "tag8",
          "tag9",
          "tag10"
        };

    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn((Class) Trade.class);

    EvaluationContext evaluationContext = new StandardEvaluationContext();
    for (int i = 0; i < params.length; i++) {
      evaluationContext.setVariable(paramNames[i], params[i]);
    }
    when(valueEvaluationContext.getEvaluationContext()).thenReturn(evaluationContext);
    when(this.evaluationContextProvider.getEvaluationContext(any(), any()))
        .thenReturn(evaluationContext);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Trade.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              Map<String, Value> paramMap = statement.getParameters();

              assertThat(paramMap.get("tag0").getString()).isEqualTo(params[0]);
              // params[1] is this.pageable that is ignored, hence no synthetic tag is created for
              // it
              assertThat(paramMap.get("tag1").getString()).isEqualTo(params[2]);
              assertThat(paramMap.get("tag2").getString()).isEqualTo(params[3]);
              assertThat(paramMap.get("tag3").getFloat64()).isEqualTo(params[4]);
              assertThat(paramMap.get("tag4").getFloat64()).isEqualTo(params[5]);
              assertThat(paramMap.get("tag5").getString()).isEqualTo(params[6]);
              assertThat(paramMap.get("tag6").getFloat64()).isEqualTo(params[7]);
              assertThat(paramMap.get("tag7").getFloat64()).isEqualTo(params[8]);
              assertThat(paramMap.get("tag8").getStruct()).isEqualTo(params[9]);
              assertThat(paramMap.get("tag10").getStringArray()).isEqualTo(params[11]);
              verify(this.spannerEntityProcessor, times(1)).write(same(params[10]), any());

              assertThat(paramMap.get("SpELtag1").getFloat64()).isEqualTo(-8.88, DELTA);
              assertThat(paramMap.get("SpELtag2").getFloat64()).isEqualTo(-3.33, DELTA);

              return null;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method method =
        QueryHolder.class.getMethod(
            "dummyMethod",
            Object.class,
            Pageable.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            Object.class,
            List.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));
    sqlSpannerQuery.execute(params);

    verify(this.spannerTemplate, times(1)).executeQuery(any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void dmlTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {
    String sql = "dml statement here";

    TransactionContext context = mock(TransactionContext.class);
    TransactionRunner transactionRunner = mock(TransactionRunner.class);
    when(this.databaseClient.readWriteTransaction()).thenReturn(transactionRunner);

    when(valueEvaluationContext.getEvaluationContext()).thenReturn(mock(EvaluationContext.class));

    when(transactionRunner.run(any()))
        .thenAnswer(
            invocation -> {
              TransactionRunner.TransactionCallable transactionCallable = invocation.getArgument(0);
              return transactionCallable.run(context);
            });

    Method method = QueryHolder.class.getMethod("noParamMethod");

    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    SqlSpannerQuery sqlSpannerQuery = spy(createQuery(sql, Trade.class, true, useValueExpressionDelegate));

    doReturn(long.class).when(sqlSpannerQuery).getReturnedSimpleConvertableItemType();
    doReturn(null).when(sqlSpannerQuery).convertToSimpleReturnType(any(), any());

    sqlSpannerQuery.execute(new Object[] {});

    verify(this.spannerTemplate, times(1)).executeDmlStatement(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void sqlCountWithWhereTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {
    String sql =
        "SELECT count(1) FROM"
            + " :com.google.cloud.spring.data.spanner.repository.query.SqlSpannerQueryTests$Child:"
            + " WHERE id = @id AND trader_id = @trader_id";

    String entityResolvedSql =
        "SELECT count(1) FROM children WHERE id = @id AND trader_id = @trader_id";

    Object[] params = new Object[] {"ID", "TRADER_ID"};
    String[] paramNames = new String[] {"id", "trader_id"};

    when(queryMethod.isCollectionQuery()).thenReturn(false);
    when(queryMethod.getReturnedObjectType()).thenReturn((Class) long.class);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, long.class, false, useValueExpressionDelegate);

    Struct row = mock(Struct.class);
    when(row.getType())
        .thenReturn(Type.struct(Arrays.asList(Type.StructField.of("STRUCT", Type.int64()))));
    when(row.getLong(0)).thenReturn(3L);
    when(row.getColumnType(0)).thenReturn(Type.int64());

    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(true).thenReturn(false);
    when(resultSet.getCurrentRowAsStruct()).thenReturn(row);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(0);
              SpannerQueryOptions queryOptions = invocation.getArgument(1);
              assertThat(queryOptions.isAllowPartialRead()).isTrue();

              assertThat(statement.getSql()).isEqualTo(entityResolvedSql);

              Map<String, Value> paramMap = statement.getParameters();

              assertThat(paramMap.get("id").getString()).isEqualTo(params[0]);
              assertThat(paramMap.get("traderId").getString()).isEqualTo(params[1]);
              return resultSet;
            })
        .when(this.spannerTemplate)
        .executeQuery(any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method method = QueryHolder.class.getMethod("dummyMethod3", String.class, String.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(method);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(method)));

    when(sqlSpannerQuery.getReturnedSimpleConvertableItemType()).thenReturn(long.class);

    sqlSpannerQuery.execute(params);

    verify(this.spannerTemplate).query((Function<Struct, Object>) any(), any(), any());
    verify(this.spannerTemplate).executeQuery(any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void sqlReturnTypeIsJsonFieldTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {
    String sql = "SELECT details from singer where stageName = @stageName";

    Object[] params = new Object[] {"STAGENAME"};
    String[] paramNames = new String[] {"stageName"};

    when(queryMethod.isCollectionQuery()).thenReturn(true);
    ResultProcessor resultProcessor = mock(ResultProcessor.class);
    ReturnedType returnedType = mock(ReturnedType.class);
    when(this.queryMethod.getResultProcessor()).thenReturn(resultProcessor);
    when(resultProcessor.getReturnedType()).thenReturn(returnedType);
    when(returnedType.getReturnedType()).thenReturn((Class) Detail.class);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Singer.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(1);
              assertThat(statement.getSql()).isEqualTo(sql);
              Map<String, Value> paramMap = statement.getParameters();
              assertThat(paramMap.get("stageName").getString()).isEqualTo(params[0]);

              return null;
            })
        .when(this.spannerTemplate)
        .query((Function<Struct, Object>) any(), any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method arrayParameterTriggeringMethod =
        QueryHolder.class.getMethod("dummyMethod6", String.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(arrayParameterTriggeringMethod);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(arrayParameterTriggeringMethod)));

    sqlSpannerQuery.execute(params);
    // capturing the row function and verifying it's the correct one with mock data
    ArgumentCaptor<Function<Struct, Object>> argumentCaptor =
        ArgumentCaptor.forClass(Function.class);
    verify(this.spannerTemplate).query(argumentCaptor.capture(), any(), any());
    Function<Struct, Object> rowFunc = argumentCaptor.getValue();

    Struct row = mock(Struct.class);
    when(row.getType())
        .thenReturn(Type.struct(Arrays.asList(Type.StructField.of("details", Type.json()))));
    when(row.getColumnType(0)).thenReturn(Type.json());
    when(row.getJson(0)).thenReturn("{\"p1\":\"address line\",\"p2\":\"5\"}");
    when(row.getColumnType("detailsList")).thenReturn(Type.array(Type.json()));

    Object result = rowFunc.apply(row);

    assertThat(result).isInstanceOf(Detail.class);
    assertThat(((Detail) result).p1).isEqualTo("address line");
    assertThat(((Detail) result).p2).isEqualTo("5");
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void sqlReturnTypeIsArrayJsonFieldTest(boolean useValueExpressionDelegate) throws NoSuchMethodException {
    String sql = "SELECT detailsList from singer where stageName = @stageName";

    Object[] params = new Object[] {"STAGENAME"};
    String[] paramNames = new String[] {"stageName"};

    when(queryMethod.isCollectionQuery()).thenReturn(true);
    ResultProcessor resultProcessor = mock(ResultProcessor.class);
    ReturnedType returnedType = mock(ReturnedType.class);
    when(this.queryMethod.getResultProcessor()).thenReturn(resultProcessor);
    when(resultProcessor.getReturnedType()).thenReturn(returnedType);
    when(returnedType.getReturnedType()).thenReturn((Class) Detail.class);

    SqlSpannerQuery sqlSpannerQuery = createQuery(sql, Singer.class, false, useValueExpressionDelegate);

    doAnswer(
            invocation -> {
              Statement statement = invocation.getArgument(1);
              assertThat(statement.getSql()).isEqualTo(sql);
              Map<String, Value> paramMap = statement.getParameters();
              assertThat(paramMap.get("stageName").getString()).isEqualTo(params[0]);

              return null;
            })
        .when(this.spannerTemplate)
        .query((Function<Struct, Object>) any(), any(), any());

    // This dummy method was created so the metadata for the ARRAY param inner type is
    // provided.
    Method arrayParameterTriggeringMethod =
        QueryHolder.class.getMethod("dummyMethod6", String.class);
    when(this.queryMethod.getQueryMethod()).thenReturn(arrayParameterTriggeringMethod);
    Mockito.<Parameters>when(this.queryMethod.getParameters())
        .thenReturn(new DefaultParameters(ParametersSource.of(arrayParameterTriggeringMethod)));

    sqlSpannerQuery.execute(params);
    // capturing the row function and verifying it's the correct one with mock data
    ArgumentCaptor<Function<Struct, Object>> argumentCaptor =
        ArgumentCaptor.forClass(Function.class);
    verify(this.spannerTemplate).query(argumentCaptor.capture(), any(), any());
    Function<Struct, Object> rowFunc = argumentCaptor.getValue();

    Struct row = mock(Struct.class);
    when(row.getType())
        .thenReturn(
            Type.struct(
                Arrays.asList(Type.StructField.of("detailsList", Type.array(Type.json())))));
    when(row.getColumnType(0)).thenReturn(Type.array(Type.json()));
    when(row.getJsonList(0))
        .thenReturn(
            Arrays.asList(
                "{\"p1\":\"address line\",\"p2\":\"5\"}",
                "{\"p1\":\"address line 2\",\"p2\":\"6\"}"));
    when(row.getColumnType("detailsList")).thenReturn(Type.array(Type.json()));

    Object result = rowFunc.apply(row);

    assertThat(result).isInstanceOf(List.class);
    assertThat((List<Detail>) result)
        .hasSize(2)
        .containsExactly(new Detail("address line", "5"), new Detail("address line 2", "6"));
  }

  private static class Singer {
    @PrimaryKey String id;

    String stageName;

    @Column(spannerType = TypeCode.JSON)
    Detail details;

    @Column(spannerType = TypeCode.JSON)
    List<Detail> detailsList;
  }

  private class Detail {
    String p1;

    String p2;

    Detail(String p1, String p2) {
      this.p1 = p1;
      this.p2 = p2;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Detail)) {
        return false;
      }
      Detail detail = (Detail) o;
      return Objects.equal(p1, detail.p1) && Objects.equal(p2, detail.p2);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(p1, p2);
    }
  }

  private static class SymbolAction {
    String symbol;

    String action;

    SymbolAction(String s, String a) {
      this.symbol = s;
      this.action = a;
    }
  }

  @Table(name = "trades")
  private static class Trade {
    @PrimaryKey String id;

    String action;

    Double price;

    Double shares;

    @Column(name = "ticker")
    String symbol;

    @Column(name = "trader_id")
    String traderId;

    @Interleaved List<Child> children;
  }

  @Table(name = "children")
  @Where("disabled = false")
  private static class Child {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    String childId;

    String value;

    boolean disabled;

    @Interleaved List<Document> documents;
  }

  @Table(name = "documents")
  @Where("canceled = false")
  private static class Document {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    String childId;

    @PrimaryKey(keyOrder = 3)
    String documentId;

    String content;

    boolean canceled;
  }

  private static class QueryHolder {
    public long dummyMethod(
        Object tag0,
        Pageable pageable,
        Object tag1,
        Object tag2,
        Object tag3,
        Object tag4,
        Object tag5,
        Object tag6,
        Object tag7,
        Object tag8,
        Object tag9,
        @Param("tag10") List<String> blahblah) {
      // tag10 is intentionally named via annotation.
      return 0;
    }

    public long dummyMethod2() {
      return 0;
    }

    public long dummyMethod3(String id, String traderId) {
      return 0;
    }

    public List<Child> dummyMethod4(String id, String traderId, Pageable param3) {
      return null;
    }

    public List<Child> dummyMethod5(String id, String traderId, Sort param3) {
      return null;
    }

    public Detail dummyMethod6(String stageName) {
      return null;
    }

    public List<Child> pageableAndSort(Pageable param1, Sort param2) {
      return null;
    }

    public List<Child> doublePageable(Pageable param1, Pageable param2) {
      return null;
    }

    public void noParamMethod() {}

    public List<Child> sortAndPageable(String id, String traderId, Sort sort, Pageable pageable) {
      return null;
    }
  }
}

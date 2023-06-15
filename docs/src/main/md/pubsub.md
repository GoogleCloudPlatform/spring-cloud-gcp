## Cloud Pub/Sub

Spring Framework on Google Cloud provides an abstraction layer to publish to and
subscribe from Google Cloud Pub/Sub topics and to create, list or delete
Google Cloud Pub/Sub topics and subscriptions.

A Spring Boot starter is provided to autoconfigure the various required
Pub/Sub components.

Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-pubsub</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
    }

This starter is also available from [Spring
Initializr](https://start.spring.io) through the `GCP Messaging` entry.

### Configuration

The Spring Boot starter for Google Cloud Pub/Sub provides the following
configuration options.

#### Spring Framework on Google Cloud Pub/Sub API Configuration

This section describes options for enabling the integration, specifying
the Google Cloud project and credentials, and setting whether the APIs should
connect to an emulator for local testing.

|                                                   |                                                                                                                                                                                                       |          |                                          |
| ------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ---------------------------------------- |
| Name                                              | Description                                                                                                                                                                                           | Required | Default value                            |
| `spring.cloud.gcp.pubsub.enabled`                 | Enables or disables Pub/Sub auto-configuration                                                                                                                                                        | No       | `true`                                   |
| `spring.cloud.gcp.pubsub.project-id`              | Google Cloud project ID where the Google Cloud Pub/Sub API is hosted, if different from the one in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                                                  | No       |                                          |
| `spring.cloud.gcp.pubsub.credentials.location`    | OAuth2 credentials for authenticating with the Google Cloud Pub/Sub API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                                     | No       |                                          |
| `spring.cloud.gcp.pubsub.emulator-host`           | The host and port of the local running emulator. If provided, this will setup the client to connect against a running [Google Cloud Pub/Sub Emulator](https://cloud.google.com/pubsub/docs/emulator). | No       |                                          |
| `spring.cloud.gcp.pubsub.credentials.encoded-key` | Base64-encoded contents of OAuth2 account private key for authenticating with the Google Cloud Pub/Sub API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)  | No       |                                          |
| `spring.cloud.gcp.pubsub.credentials.scopes`      | [OAuth2 scope](https://developers.google.com/identity/protocols/googlescopes) for Spring Framework on Google Cloud Pub/Sub credentials                                                                                | No       | <https://www.googleapis.com/auth/pubsub> |

#### Publisher/Subscriber Configuration

This section describes configuration options to customize the behavior
of the application’s Pub/Sub publishers and subscribers. Subscriber
settings can be either global or subscription-specific.

<div class="note">

A custom configuration (injected through a setter in
`DefaultSubscriberFactory` or a custom bean) will take precedence over
autoconfiguration. Hence, if one wishes to use per-subscription
configuration for a Pub/Sub setting, there must not be a custom bean for
that setting. When using autoconfiguration, if both global and
per-subscription configurations are provided, then the per-subscription
configuration will be used. However, if a per-subscription configuration
is not set then the global or default configuration will be used.

</div>

|                                                                                                      |                                                                                                                                                                                                                                                              |          |                           |
| ---------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- | ------------------------- |
| Name                                                                                                 | Description                                                                                                                                                                                                                                                  | Required | Default value             |
| `spring.cloud.gcp.pubsub.subscriber.parallel-pull-count`                                             | The number of pull workers.                                                                                                                                                                                                                                  | No       | 1                         |
| `spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period`                                        | The maximum period a message ack deadline will be extended, in seconds.                                                                                                                                                                                      | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscriber.min-duration-per-ack-extension`                                  | The lower bound for a single mod ack extension period, in seconds.                                                                                                                                                                                           | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscriber.max-duration-per-ack-extension`                                  | The upper bound for a single mod ack extension period, in seconds.                                                                                                                                                                                           | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscriber.pull-endpoint`                                                   | The endpoint for pulling messages.                                                                                                                                                                                                               | No       | pubsub.googleapis.com:443 |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].executor-threads`                                    | Number of threads used by `Subscriber` instances created by `SubscriberFactory`.                                                                                                                                                                             | No       | 4                         |
| `spring.cloud.gcp.pubsub.[subscriber,publisher.batching].flow-control.max-outstanding-element-count` | Maximum number of outstanding elements to keep in memory before enforcing flow control.                                                                                                                                                                      | No       | unlimited                 |
| `spring.cloud.gcp.pubsub.[subscriber,publisher.batching].flow-control.max-outstanding-request-bytes` | Maximum number of outstanding bytes to keep in memory before enforcing flow control.                                                                                                                                                                         | No       | unlimited                 |
| `spring.cloud.gcp.pubsub.[subscriber,publisher.batching].flow-control.limit-exceeded-behavior`       | The behavior when the specified limits are exceeded.                                                                                                                                                                                                         | No       | Block                     |
| `spring.cloud.gcp.pubsub.publisher.batching.element-count-threshold`                                 | The element count threshold to use for batching.                                                                                                                                                                                                             | No       | 1 (batching off)          |
| `spring.cloud.gcp.pubsub.publisher.batching.request-byte-threshold`                                  | The request byte threshold to use for batching.                                                                                                                                                                                                              | No       | 1 byte (batching off)     |
| `spring.cloud.gcp.pubsub.publisher.batching.delay-threshold-seconds`                                 | The delay threshold to use for batching. After this amount of time has elapsed (counting from the first element added), the elements will be wrapped up in a batch and sent.                                                                                 | No       | 1 ms (batching off)       |
| `spring.cloud.gcp.pubsub.publisher.batching.enabled`                                                 | Enables batching.                                                                                                                                                                                                                                            | No       | false                     |
| `spring.cloud.gcp.pubsub.publisher.enable-message-ordering`                                          | Enables message ordering.                                                                                                                                                                                                                                    | No       | false                     |
| `spring.cloud.gcp.pubsub.publisher.endpoint`                                                         | The publisher endpoint. Example: `"us-east1-pubsub.googleapis.com:443"`. This is useful in conjunction with enabling message ordering because sending messages to the same region ensures they are received in order even when multiple publishers are used. | No       | pubsub.googleapis.com:443 |

##### Subscription-specific Configurations

|                                                                                                       |                                                                                                                                                                                                                                                       |          |                           |
|-------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| -------- | ------------------------- |
| Name                                                                                                  | Description                                                                                                                                                                                                                                           | Required | Default value             |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].fully-qualified-name`                       | The fully-qualified subscription name in the `projects/[PROJECT]/subscriptions/[SUBSCRIPTION]` format. When this property is present, the `[subscription-name]` key does not have to match any actual resources; it’s used only for logical grouping. | No       | 1                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].parallel-pull-count`                        | The number of pull workers.                                                                                                                                                                                                                           | No       | 1                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].max-ack-extension-period`                   | The maximum period a message ack deadline will be extended, in seconds.                                                                                                                                                                               | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].min-duration-per-ack-extension`             | The lower bound for a single mod ack extension period, in seconds.                                                                                                                                                                                    | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].max-duration-per-ack-extension`             | The upper bound for a single mod ack extension period, in seconds.                                                                                                                                                                                    | No       | 0                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].pull-endpoint`                              | The endpoint for pulling messages.                                                                                                                                                                                                        | No       | pubsub.googleapis.com:443 |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].executor-threads`                           | Number of threads used by `Subscriber` instances created by `SubscriberFactory`. Note that configuring per-subscription `executor-threads` will result in the creation of thread pools for both global/default **and** per-subscription configurations. | No       | 4                         |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].flow-control.max-outstanding-element-count` | Maximum number of outstanding elements to keep in memory before enforcing flow control.                                                                                                                                                               | No       | unlimited                 |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].flow-control.max-outstanding-request-bytes` | Maximum number of outstanding bytes to keep in memory before enforcing flow control.                                                                                                                                                                  | No       | unlimited                 |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].flow-control.limit-exceeded-behavior`       | The behavior when the specified limits are exceeded.                                                                                                                                                                                                  | No       | Block                     |

<div class="note">

By default, subscription-specific threads are named after
fully-qualified subscription name, ex:
`gcp-pubsub-subscriber-projects/project-id/subscriptions/subscription-name`.
This can be customized, by registering a
`SelectiveSchedulerThreadNameProvider` bean.

</div>

#### GRPC Connection Settings

The Pub/Sub API uses the
[GRPC](https://cloud.google.com/pubsub/docs/reference/service_apis_overview#grpc_api)
protocol to send API requests to the Pub/Sub service. This section
describes configuration options for customizing the GRPC behavior.

<div class="note">

The properties that refer to `retry` control the RPC retries for
transient failures during the gRPC call to Cloud Pub/Sub server. They do
**not** control message redelivery; only message acknowledgement
deadline can be used to extend or shorten the amount of time until
Pub/Sub attempts redelivery.

</div>

|                                                                                    |                                                                                                                                                                                                                                               |          |                             |
| ---------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | --------------------------- |
| Name                                                                               | Description                                                                                                                                                                                                                                   | Required | Default value               |
| `spring.cloud.gcp.pubsub.keepAliveIntervalMinutes`                                 | Determines frequency of keepalive gRPC ping                                                                                                                                                                                                   | No       | `5 minutes`                 |
| `spring.cloud.gcp.pubsub.subscriber.retryableCodes`                                | RPC status codes that should be retried when pulling messages.                                                                                                                                                                                | No       | UNKNOWN,ABORTED,UNAVAILABLE |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.total-timeout-seconds`       | TotalTimeout has ultimate control over how long the logic should keep trying the remote call until it gives up completely. The higher the total timeout, the more retries can be attempted.                                                   | No       | 0                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.initial-retry-delay-second`  | InitialRetryDelay controls the delay before the first retry. Subsequent retries will use this value adjusted according to the RetryDelayMultiplier.                                                                                           | No       | 0                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.retry-delay-multiplier`      | RetryDelayMultiplier controls the change in retry delay. The retry delay of the previous call is multiplied by the RetryDelayMultiplier to calculate the retry delay for the next call.                                                       | No       | 1                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.max-retry-delay-seconds`     | MaxRetryDelay puts a limit on the value of the retry delay, so that the RetryDelayMultiplier can’t increase the retry delay higher than this amount.                                                                                          | No       | 0                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.max-attempts`                | MaxAttempts defines the maximum number of attempts to perform. If this value is greater than 0, and the number of attempts reaches this limit, the logic will give up retrying even if the total retry time is still lower than TotalTimeout. | No       | 0                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.jittered`                    | Jitter determines if the delay time should be randomized.                                                                                                                                                                                     | No       | true                        |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.initial-rpc-timeout-seconds` | InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use this value adjusted according to the RpcTimeoutMultiplier.                                                                                              | No       | 0                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.rpc-timeout-multiplier`      | RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous call is multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next call.                                                               | No       | 1                           |
| `spring.cloud.gcp.pubsub.[subscriber,publisher].retry.max-rpc-timeout-seconds`     | MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the RpcTimeoutMultiplier can’t increase the RPC timeout higher than this amount.                                                                                          | No       | 0                           |

Subscription-specific Configuration

|                                                                                              |                                                                                                                                                                                                                                               |          |                             |
| -------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | --------------------------- |
| Name                                                                                         | Description                                                                                                                                                                                                                                   | Required | Default value               |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retryableCodes`                    | RPC status codes that should be retried when pulling messages.                                                                                                                                                                                | No       | UNKNOWN,ABORTED,UNAVAILABLE |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.total-timeout-seconds`       | TotalTimeout has ultimate control over how long the logic should keep trying the remote call until it gives up completely. The higher the total timeout, the more retries can be attempted.                                                   | No       | 0                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.initial-retry-delay-second`  | InitialRetryDelay controls the delay before the first retry. Subsequent retries will use this value adjusted according to the RetryDelayMultiplier.                                                                                           | No       | 0                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.retry-delay-multiplier`      | RetryDelayMultiplier controls the change in retry delay. The retry delay of the previous call is multiplied by the RetryDelayMultiplier to calculate the retry delay for the next call.                                                       | No       | 1                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.max-retry-delay-seconds`     | MaxRetryDelay puts a limit on the value of the retry delay, so that the RetryDelayMultiplier can’t increase the retry delay higher than this amount.                                                                                          | No       | 0                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.max-attempts`                | MaxAttempts defines the maximum number of attempts to perform. If this value is greater than 0, and the number of attempts reaches this limit, the logic will give up retrying even if the total retry time is still lower than TotalTimeout. | No       | 0                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.jittered`                    | Jitter determines if the delay time should be randomized.                                                                                                                                                                                     | No       | true                        |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.initial-rpc-timeout-seconds` | InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use this value adjusted according to the RpcTimeoutMultiplier.                                                                                              | No       | 0                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.rpc-timeout-multiplier`      | RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous call is multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next call.                                                               | No       | 1                           |
| `spring.cloud.gcp.pubsub.subscription.[subscription-name].retry.max-rpc-timeout-seconds`     | MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the RpcTimeoutMultiplier can’t increase the RPC timeout higher than this amount.                                                                                          | No       | 0                           |

#### Programmatic Configuration

To apply publishing customizations not covered by the properties above,
you may provide custom beans of type `PublisherCustomizer` to
post-process the `Publisher.Builder` object right before it is built
into a `Publisher`. The `PublisherCustomizer` beans may be annotated
with Spring Framework’s `@Order` annotation to ensure they are applied
in a particular sequence.

### Spring Boot Actuator Support

#### Cloud Pub/Sub Health Indicator

If you are using Spring Boot Actuator, you can take advantage of the
Cloud Pub/Sub health indicator called `pubsub`. The health indicator
will verify whether Cloud Pub/Sub is up and accessible by your
application. To enable it, all you need to do is add the [Spring Boot
Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)
to your project.

The `pubsub` indicator will then roll up to the overall application
status visible at <http://localhost:8080/actuator/health> (use the
`management.endpoint.health.show-details` property to view per-indicator
details).

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

<div class="note">

If your application already has actuator and Cloud Pub/Sub starters,
this health indicator is enabled by default. To disable the Cloud
Pub/Sub indicator, set `management.health.pubsub.enabled` to `false`.

</div>

The health indicator validates the connection to Pub/Sub by pulling
messages from a Pub/Sub subscription.

If no subscription has been specified via
`spring.cloud.gcp.pubsub.health.subscription`, it will pull messages
from a random subscription that is expected not to exist. It will signal
"up" if it is able to connect to Spring Framework on Google Cloud Pub/Sub APIs, i.e. the pull results
in a response of `NOT_FOUND` or `PERMISSION_DENIED`.

If a custom subscription has been specified, this health indicator will
signal "up" if messages are successfully pulled and (optionally)
acknowledged, or when a successful pull is performed but no messages are
returned from Pub/Sub. Note that messages pulled from the subscription
will not be acknowledged, unless you set the
`spring.cloud.gcp.pubsub.health.acknowledge-messages` option to `true`.
So, take care not to configure a subscription that has a business
impact, or instead leave the custom subscription out completely.

|                                                       |                                                                                   |          |                                                     |
| ----------------------------------------------------- | --------------------------------------------------------------------------------- | -------- | --------------------------------------------------- |
| Name                                                  | Description                                                                       | Required | Default value                                       |
| `management.health.pubsub.enabled`                    | Whether to enable the Pub/Sub health indicator                                    | No       | `true` with Spring Boot Actuator, `false` otherwise |
| `spring.cloud.gcp.pubsub.health.subscription`         | Subscription to health check against by pulling a message                         | No       | Random non-existent                                 |
| `spring.cloud.gcp.pubsub.health.timeout-millis`       | Milliseconds to wait for response from Pub/Sub before timing out                  | No       | `2000`                                              |
| `spring.cloud.gcp.pubsub.health.acknowledge-messages` | Whether to acknowledge messages pulled from the optionally specified subscription | No       | `false`                                             |

#### Cloud Pub/Sub Subscription Health Indicator

If you are using Spring Boot Actuator, you can take advantage of the
Cloud Pub/Sub subscription health indicator called `pubsub-subscriber`.
The subscription health indicator will verify whether Pub/Sub
subscriptions are actively processing messages from the subscription’s
backlog. To enable it, you need to add the [Spring Boot
Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)
to your project and the [GCP
Monitoring](https://cloud.google.com/monitoring/docs/reference/libraries).
Also, you need to set the following properties
`spring.cloud.gcp.pubsub.health.lagThreshold`,
`spring.cloud.gcp.pubsub.health.backlogThreshold`.

The `pubsub-subscriber` indicator will then roll up to the overall
application status visible at <http://localhost:8080/actuator/health>
(use the `management.endpoint.health.show-details` property to view
per-indicator details).

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-monitoring</artifactId>
</dependency>
```

The health indicator validates a subscriber’s health by checking the
subscription’s message backlog and the last processed message. A
subscription’s backlog is retrieved using Google Cloud’s Monitoring
Metrics. The metric used is the `num_undelivered_messages` for a
subscription.

If a message has been recently processed in a reasonable time threshold,
then the subscriber is healthy. If the backlog of messages for a
subscription is big but the subscriber consumes messages then subscriber
is still healthy. If there hasn’t been any processing of recent messages
but the backlog increases, then the subscriber is unhealthy.

<div class="note">

The health indicator will not behave entirely as expected if Dead Letter
Queueing is enabled on the subscription being checked,
num\_undelivered\_messages will drop by itself after DLQ threshold
is reached.

</div>

|                                                   |                                                                  |          |                                                     |
| ------------------------------------------------- | ---------------------------------------------------------------- | -------- | --------------------------------------------------- |
| Name                                              | Description                                                      | Required | Default value                                       |
| `management.health.pubsub-subscriber.enabled`     | Whether to enable the Pub/Sub Subscription health indicator      | No       | `true` with Spring Boot Actuator, `false` otherwise |
| `spring.cloud.gcp.pubsub.health.lagThreshold`     | Threshold in seconds over message processing lag                 | Yes      | Provided                                            |
| `spring.cloud.gcp.pubsub.health.backlogThreshold` | The threshold number of messages for a subscription backlog      | Yes      | Provided                                            |
| `spring.cloud.gcp.pubsub.health.lookUpInterval`   | The optional interval in seconds for subscription backlog lookup | No       | `1`                                                 |
| `spring.cloud.gcp.pubsub.health.executorThreads`  | Number of threads used for Health Check Executors                | No       | `4`                                                 |

### Pub/Sub Operations & Template

`PubSubOperations` is an abstraction that allows Spring users to use
Google Cloud Pub/Sub without depending on any Google Cloud Pub/Sub API
semantics. It provides the common set of operations needed to interact
with Google Cloud Pub/Sub. `PubSubTemplate` is the default
implementation of `PubSubOperations` and it uses the [Google Cloud Java
Client for
Pub/Sub](https://github.com/GoogleCloudPlatform/google-cloud-java/tree/main/google-cloud-pubsub)
to interact with Google Cloud Pub/Sub.

#### Publishing to a topic

`PubSubTemplate` provides asynchronous methods to publish messages to a
Google Cloud Pub/Sub topic. The `publish()` method takes in a topic name
to post the message to, a payload of a generic type and, optionally, a
map with the message headers. The topic name could either be a short
topic name within the current project, or the fully-qualified name
referring to a topic in a different project using the
`projects/[project_name]/topics/[topic_name]` format.

Here is an example of how to publish a message to a Google Cloud Pub/Sub
topic:

``` java
Map<String, String> headers = Collections.singletonMap("key1", "val1");
pubSubTemplate.publish(topicName, "message", headers).get();
```

By default, the `SimplePubSubMessageConverter` is used to convert
payloads of type `byte[]`, `ByteString`, `ByteBuffer`, and `String` to
Pub/Sub messages.

##### Ordering messages

If you are relying on message converters and would like to provide an
ordering key, use the `GcpPubSubHeaders.ORDERING_KEY` header. You will
also need to make sure to enable message ordering on the publisher via
the `spring.cloud.gcp.pubsub.publisher.enable-message-ordering`
property. Additionally, if you are using multiple publishers, you will
want to set the `spring.cloud.gcp.pubsub.publisher.endpoint` to a
regional endpoint such as `"us-east1-pubsub.googleapis.com:443"` so that
messages are sent to the same region and received in order.

``` java
Map<String, String> headers =
    Collections.singletonMap(GcpPubSubHeaders.ORDERING_KEY, "key1");
pubSubTemplate.publish(topicName, "message1", headers).get();
pubSubTemplate.publish(topicName, "message2", headers).get();
```

#### Subscribing to a subscription

Google Cloud Pub/Sub allows many subscriptions to be associated to the
same topic. `PubSubTemplate` allows you to listen to subscriptions via
the `subscribe()` method. When listening to a subscription, messages
will be pulled from Google Cloud Pub/Sub asynchronously and passed to a
user provided message handler. The subscription name could either be a
short subscription name within the current project, or the
fully-qualified name referring to a subscription in a different project
using the `projects/[project_name]/subscriptions/[subscription_name]`
format.

##### Example

Subscribe to a subscription with a message handler:

``` java
Subscriber subscriber =
    pubSubTemplate.subscribe(
        subscriptionName,
        message -> {
          logger.info(
              "Message received from "
                  + subscriptionName
                  + " subscription: "
                  + message.getPubsubMessage().getData().toStringUtf8());
          message.ack();
        });
```

##### Subscribe methods

`PubSubTemplate` provides the following subscribe methods:

|                                                                                                                                                   |                                                                                                              |
| ------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| **subscribe(String subscription, Consumer\<BasicAcknowledgeablePubsubMessage\> messageConsumer)**                                                 | asynchronously pulls messages and passes them to `messageConsumer`                                           |
| **subscribeAndConvert(String subscription, Consumer\<ConvertedBasicAcknowledgeablePubsubMessage\<T\>\> messageConsumer, Class\<T\> payloadType)** | same as `pull`, but converts message payload to `payloadType` using the converter configured in the template |

<div class="note">
As of version 1.2, subscribing by itself is not enough to keep an application running.
For a command-line application, a way to keep the application running is to have a user thread(non-daemon thread) started up. A fake scheduled task creates a threadpool with non-daemon threads:

```java
@Scheduled (fixedRate = 1, timeUnit = TimeUnit.MINUTES)
public void fakeScheduledTask() {
    // do nothing
}
```

Another option is to pull in `spring-boot-starter-web` or `spring-boot-starter-webflux` as a dependency which will start an embedded servlet container or reactive server keeping the application running in the background
</div>

#### Pulling messages from a subscription

Google Cloud Pub/Sub supports synchronous pulling of messages from a
subscription. This is different from subscribing to a subscription, in
the sense that subscribing is an asynchronous task.

##### Example

Pull up to 10 messages:

``` java
int maxMessages = 10;
boolean returnImmediately = false;
List<AcknowledgeablePubsubMessage> messages =
    pubSubTemplate.pull(subscriptionName, maxMessages, returnImmediately);

// acknowledge the messages
pubSubTemplate.ack(messages);

messages.forEach(
    message ->
        logger.info(message.getPubsubMessage().getData().toStringUtf8()));
```

##### Pull methods

`PubsubTemplate` provides the following pull methods:

| Method                                                                          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|---------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **pull\(String subscription, Integer maxMessages, Boolean returnImmediately\)** | Pulls a number of messages from a subscription, allowing for the retry settings to be configured\. Any messages received by `pull()` are not automatically acknowledged\. See Acknowledging messages section\. <br/>The `maxMessages` parameter is the maximum limit of how many messages to pull from a subscription in a single call; this value must be greater than 0\. You may omit this parameter by passing in `null`; this means there will be no limit on the number of messages pulled \(`maxMessages` will be `Integer.MAX_INTEGER`)\. <br/> If `returnImmediately` is `true`, the system will respond immediately even if it there are no messages available to return in the `Pull` response\. Otherwise, the system may wait \(for a bounded amount of time\) until at least one message is available, rather than returning no messages\. |
| **pullAndAck**                                                                  | Works the same as the `pull` method and, additionally, acknowledges all received messages\.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **pullNext**                                                                    | Allows for a single message to be pulled and automatically acknowledged from a subscription\.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **pullAndConvert**                                                              | Works the same as the `pull` method and, additionally, converts the Pub/Sub binary payload to an object of the desired type, using the converter configured in the template\.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |



<div class="warning">

We do not recommend setting `returnImmediately` to `true`, as it may
result in delayed message delivery. "Immediately" really means 1 second,
and if Pub/Sub cannot retrieve any messages from the backend in that
time, it will return 0 messages, despite having messages queue up on the
topic. Therefore, we recommend setting `returnImmediately` to `false`,
or using `subscribe` methods from the previous section.

</div>



##### Acknowledging messages

There are two ways to acknowledge messages.

1.  To acknowledge multiple messages at once, you can use the
    `PubSubTemplate.ack()` method. You can also use the
    `PubSubTemplate.nack()` for negatively acknowledging messages. Using
    these methods for acknowledging messages in batches is more
    efficient than acknowledging messages individually, but they
    **require** the collection of messages to be from the same project.

2.  To acknowledge messages individually you can use the `ack()` or
    `nack()` method on each of them (to acknowledge or negatively
    acknowledge, correspondingly).

<div class="note">

All `ack()`, `nack()`, and `modifyAckDeadline()` methods on messages, as
well as `PubSubSubscriberTemplate`, are implemented asynchronously,
returning a `CompletableFuture<Void>` to enable asynchronous processing.

</div>

##### Dead Letter Topics

Your application may occasionally receive a message it cannot process.
If you [create your `Subscription`](#creating-a-subscription) passing
the `Subscription.Builder` argument, you can specify a
`DeadLetterPolicy` that will forward all `nack()`-ed and non-`ack()`-ed
messages after a configurable amount of redelivery attempts. See
[here](https://cloud.google.com/pubsub/docs/dead-letter-topics) for more
information.

``` java
public Subscription newSubscription() {
    // Must use the fully-qualified topic name.
    String fullDeadLetterTopic = PubSubTopicUtils
                        .toTopicName(DEAD_LETTER_TOPIC, gcpProjectIdProvider.getProjectId())
                        .toString();
    return pubSubAdmin.createSubscription(Subscription.newBuilder()
            .setName(SUBSCRIPTION_NAME)
            .setTopic(TOPIC_NAME)
            .setDeadLetterPolicy(DeadLetterPolicy.newBuilder()
                    .setDeadLetterTopic(fullDeadLetterTopic)
                    .setMaxDeliveryAttempts(6)
                    .build()));
}
```

Dead letter topics are no different from any other topic, though some
[additional
permissions](https://cloud.google.com/pubsub/docs/dead-letter-topics#granting_forwarding_permissions)
are necessary to ensure the Cloud Pub/Sub service can successfully `ack`
the original message and re-`publish` on the dead letter topic.

#### JSON support

For serialization and deserialization of POJOs using Jackson JSON,
configure a `PubSubMessageConverter` bean, and the Spring Boot starter
for Spring Framework on Google Cloud Pub/Sub will automatically wire it into the `PubSubTemplate`.

``` java
// Note: The ObjectMapper is used to convert Java POJOs to and from JSON.
// You will have to configure your own instance if you are unable to depend
// on the ObjectMapper provided by Spring Boot starters.
@Bean
public PubSubMessageConverter pubSubMessageConverter() {
  return new JacksonPubSubMessageConverter(new ObjectMapper());
}
```

<div class="note">

Alternatively, you can set it directly by calling the
`setMessageConverter()` method on the `PubSubTemplate`. Other
implementations of the `PubSubMessageConverter` can also be configured
in the same manner.

</div>

Assuming you have the following class defined:

``` java
static class TestUser {

  String username;

  String password;

  public String getUsername() {
    return this.username;
  }

  void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  void setPassword(String password) {
    this.password = password;
  }
}
```

You can serialize objects to JSON on publish automatically:

``` java
TestUser user = new TestUser();
user.setUsername("John");
user.setPassword("password");
pubSubTemplate.publish(topicName, user);
```

And that’s how you convert messages to object on pull:

``` java
int maxMessages = 1;
boolean returnImmediately = false;
List<ConvertedAcknowledgeablePubsubMessage<TestUser>> messages =
    pubSubTemplate.pullAndConvert(
        subscriptionName, maxMessages, returnImmediately, TestUser.class);

ConvertedAcknowledgeablePubsubMessage<TestUser> message = messages.get(0);

// acknowledge the message
message.ack();

TestUser receivedTestUser = message.getPayload();
```

Please refer to our [Pub/Sub JSON Payload Sample
App](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-integration-pubsub-json-sample)
as a reference for using this functionality.

### Reactive Stream Subscriber

It is also possible to acquire a reactive stream backed by a
subscription. To do so, a Project Reactor dependency
(`io.projectreactor:reactor-core`) must be added to the project. The
combination of the Pub/Sub starter and the Project Reactor dependencies
will then make a `PubSubReactiveFactory` bean available, which can then
be used to get a `Publisher`.

``` java
@Autowired
PubSubReactiveFactory reactiveFactory;

// ...

Flux<AcknowledgeablePubsubMessage> flux
                = reactiveFactory.poll("exampleSubscription", 1000);
```

The `Flux` then represents an infinite stream of Spring Framework on Google Cloud Pub/Sub messages
coming in through the specified subscription. For unlimited demand, the
Pub/Sub subscription will be polled regularly, at intervals determined
by `pollingPeriodMs` parameter passed in when creating the `Flux`. For
bounded demand, the `pollingPeriodMs` parameter is unused. Instead, as
many messages as possible (up to the requested number) are delivered
immediately, with the remaining messages delivered as they become
available.

Any exceptions thrown by the underlying message retrieval logic will be
passed as an error to the stream. The error handling operators
(`Flux#retry()`, `Flux#onErrorResume()` etc.) can be used to recover.

The full range of Project Reactor operations can be applied to the
stream. For example, if you only want to fetch 5 messages, you can use
`limitRequest` operation to turn the infinite stream into a finite one:

``` java
Flux<AcknowledgeablePubsubMessage> fiveMessageFlux = flux.limitRequest(5);
```

Messages flowing through the `Flux` should be manually acknowledged.

``` java
flux.doOnNext(AcknowledgeablePubsubMessage::ack);
```

### Pub/Sub management

`PubSubAdmin` is the abstraction provided by Spring Framework on Google Cloud to manage
Google Cloud Pub/Sub resources. It allows for the creation, deletion and
listing of topics and subscriptions.

<div class="note">

Generally when referring to topics and subscriptions, you can either use
the short canonical name within the current project, or the
fully-qualified name referring to a topic or subscription in a different
project using the
`projects/[project_name]/(topics|subscriptions)/<name>` format.

</div>

The Spring Boot starter for Spring Framework on Google Cloud Pub/Sub auto-configures a `PubSubAdmin`
object using the `GcpProjectIdProvider` and the `CredentialsProvider`
autoconfigured by the Spring Framework on Google Cloud Core Starter.

#### Creating a topic

`PubSubAdmin` implements a method to create topics:

``` java
public Topic createTopic(String topicName)
```

Here is an example of how to create a Google Cloud Pub/Sub topic:

``` java
public void newTopic() {
    pubSubAdmin.createTopic("topicName");
}
```

#### Deleting a topic

`PubSubAdmin` implements a method to delete topics:

``` java
public void deleteTopic(String topicName)
```

Here is an example of how to delete a Google Cloud Pub/Sub topic:

``` java
public void deleteTopic() {
    pubSubAdmin.deleteTopic("topicName");
}
```

#### Listing topics

`PubSubAdmin` implements a method to list topics:

``` java
public List<Topic> listTopics
```

Here is an example of how to list every Google Cloud Pub/Sub topic name
in a project:

``` java
List<String> topics =
                pubSubAdmin.listTopics().stream().map(Topic::getName).toList();
```

#### Creating a subscription

`PubSubAdmin` implements several methods to create subscriptions to
existing topics:

``` java
public Subscription createSubscription(String subscriptionName, String topicName)

public Subscription createSubscription(String subscriptionName, String topicName, Integer ackDeadline)

public Subscription createSubscription(String subscriptionName, String topicName, String pushEndpoint)

public Subscription createSubscription(String subscriptionName, String topicName, Integer ackDeadline, String pushEndpoint)

public Subscription createSubscription(Subscriber.Builder builder)
```

The default value for `ackDeadline` is 10 seconds. If `pushEndpoint`
isn’t specified, the subscription uses message pulling, instead. You
can also pass a `Subscription.Builder` for full control over any options
or features available in the client library.

Here is an example of how to create a Google Cloud Pub/Sub subscription:

``` java
public Subscription newSubscription() {
    return pubSubAdmin.createSubscription("subscriptionName", "topicName", 15);
}
```

#### Deleting a subscription

`PubSubAdmin` implements a method to delete subscriptions:

``` java
public void deleteSubscription(String subscriptionName)
```

Here is an example of how to delete a Google Cloud Pub/Sub subscription:

``` java
public void deleteSubscription() {
    pubSubAdmin.deleteSubscription("subscriptionName");
}
```

#### Listing subscriptions

`PubSubAdmin` implements a method to list subscriptions:

``` java
public List<Subscription> listSubscriptions()
```

Here is an example of how to list every subscription name in a project:

``` java
List<String> subscriptions =
                pubSubAdmin.listSubscriptions().stream()
                    .map(Subscription::getName)
                    .toList();
```

### Sample

Sample applications for [using the
template](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-pubsub-sample)
and [using a subscription-backed reactive
stream](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-pubsub-reactive-sample)
are available.

### Test

`Testcontainers` provides a `gcloud` module which offers `PubSubEmulatorContainer`. See more at the [docs](https://www.testcontainers.org/modules/gcloud/#pubsub)

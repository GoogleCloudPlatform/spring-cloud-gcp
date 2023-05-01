## Spring Cloud Stream

Spring Framework on Google Cloud provides a [Spring Cloud
Stream](https://cloud.spring.io/spring-cloud-stream/) binder to Google
Cloud Pub/Sub.

The provided binder relies on the [Spring Integration Channel Adapters
for Google Cloud
Pub/Sub](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-pubsub/src/main/java/com/google/cloud/spring/pubsub/integration).

Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-pubsub-stream-binder</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
        implementation("com.google.cloud:spring-cloud-gcp-pubsub-stream-binder")
    }

### Overview

This binder binds producers to Google Cloud Pub/Sub topics and consumers
to subscriptions.

<div class="note">

Partitioning is currently not supported by this binder.

</div>

### Configuration

You can configure the Spring Cloud Stream Binder for Google Cloud
Pub/Sub to automatically generate the underlying resources, like the
Google Cloud Pub/Sub topics and subscriptions for producers and
consumers. For that, you can use the
`spring.cloud.stream.gcp.pubsub.bindings.<consumer/produer name>.<consumer|producer>.auto-create-resources`
property, which is turned ON by default.

<div class="note">

For more info about consumer/producer naming convention, please refer to [Functional binding names](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_functional_binding_names).

</div>

Starting with version 1.1, these and other binder properties can be
configured globally for all the bindings, e.g.
`spring.cloud.stream.gcp.pubsub.default.consumer.auto-create-resources`.

If you are using Pub/Sub autoconfiguration from the Spring Framework on Google Cloud

Pub/Sub Starter, you should refer to the
[configuration](#pubsub-configuration) section for other Pub/Sub
parameters.

<div class="note">

To use this binder with a [running
emulator](https://cloud.google.com/pubsub/docs/emulator), configure its
host and port via `spring.cloud.gcp.pubsub.emulator-host`.

</div>

#### Producer Synchronous Sending Configuration

By default, this binder will send messages to Cloud Pub/Sub
asynchronously. If synchronous sending is preferred (for example, to
allow propagating errors back to the sender), set
`spring.cloud.stream.gcp.pubsub.default.producer.sync` property to
`true`.

#### Producer Destination Configuration

If automatic resource creation is turned ON and the topic corresponding
to the destination name does not exist, it will be created.

For example, for the following configuration, a topic called `myEvents`
would be created.

**application.properties.**

    spring.cloud.stream.bindings.{PRODUCER_NAME}.destination=myEvents
    spring.cloud.stream.gcp.pubsub.bindings.{PRODUCER_NAME}.producer.auto-create-resources=true

#### Consumer Destination Configuration

A `PubSubInboundChannelAdapter` will be configured for your consumer
endpoint. You may adjust the ack mode of the consumer endpoint using the
`ack-mode` property. The ack mode controls how messages will be
acknowledged when they are successfully received. The three possible
options are: `AUTO` (default), `AUTO_ACK`, and `MANUAL`. These options
are described in detail in the [Pub/Sub channel adapter
documentation](#inbound-channel-adapter-using-pubsub-streaming-pull).

**application.properties.**

    # How to set the ACK mode of the consumer endpoint.
    spring.cloud.stream.gcp.pubsub.bindings.{CONSUMER_NAME}.consumer.ack-mode=AUTO_ACK

With automatic resource creation turned ON for a consumer, the library
creates a topic and/or a subscription if they do not exist. The topic
name becomes the same as the destination name, and the subscription name
follows these rules (in order of precedence):

  - A user-defined, pre-existing subscription (use
    `spring.cloud.stream.gcp.pubsub.bindings.{CONSUMER_NAME}.consumer.subscriptionName`)

  - A consumer group using the topic name (use
    `spring.cloud.stream.bindings.events.group` to create a subscription
    named `<topicName>.<group>`)

  - If neither of the above are specified, the library creates an
    anonymous subscription with the name
    `anonymous.<destinationName>.<randomUUID>`. Then when the binder
    shuts down, the library automatically cleans up all Pub/Sub
    subscriptions created for anonymous consumer groups.

For example, with this configuration:

**application.properties.**

    spring.cloud.stream.bindings.{CONSUMER_NAME}.destination=myEvents
    spring.cloud.stream.gcp.pubsub.bindings.{CONSUMER_NAME}.consumer.auto-create-resources=false

Only an anonymous subscription named
`anonymous.myEvents.a6d83782-c5a3-4861-ac38-e6e2af15a7be` is created and
later cleaned up.

In another example, with the following configuration:

**application.properties.**

    spring.cloud.stream.bindings.{CONSUMER_NAME}.destination=myEvents
    spring.cloud.stream.gcp.pubsub.bindings.{CONSUMER_NAME}.consumer.auto-create-resources=true
    
    # specify consumer group, and avoid anonymous consumer group generation
    spring.cloud.stream.bindings.{CONSUMER_NAME}.group=consumerGroup1

These resources will be created:

  - A topic named `myEvents`

  - A subscription named `myEvents.consumerGroup1`

#### Header Mapping

You can filter incoming and outgoing message headers with `allowHeaders`
property. For example, for a consumer to allow only two headers, provide
a comma separated list like this:

**application.properties.**

    spring.cloud.stream.gcp.pubsub.bindings.{CONSUMER_NAME}.consumer.allowedHeaders=allowed1, allowed2

Where `CONSUMER_NAME` should be replaced by the method which is
consuming/reading messages from Cloud Pub/Sub and allowed1, allowed2 is
the comma separated list of headers that the user wants to keep.

A similar style is applicable for producers as well. For example:

**application.properties.**

    spring.cloud.stream.gcp.pubsub.bindings.{PRODUCER_NAME}.producer.allowedHeaders=allowed3,allowed4

Where `PRODUCER_NAME` should be replaced by the method which is
producing/sending messages to Cloud Pub/Sub and allowed3, allowed4 is
the comma separated list of headers that user wants to map. All other
headers will be removed before the message is sent to Cloud Pub/Sub.

#### Endpoint Customization

You may customize channel routing by defining a
`ConsumerEndpointCustomizer` in your autoconfiguration. This is useful
if you want to customize the default configurations provided by the
Pub/Sub Spring Cloud Stream Binder.

The example below demonstrates how to use a `ConsumerEndpointCustomizer`
to override the default error channel configured by the binder.

``` java
@Bean
public ConsumerEndpointCustomizer<PubSubInboundChannelAdapter> messageChannelAdapter() {
    return (endpoint, destinationName, group) -> {
        NamedComponent namedComponent = (NamedComponent) endpoint.getOutputChannel();
        String channelName = namedComponent.getBeanName();
        endpoint.setErrorChannelName(channelName + ".errors");
    };
}
```

### Binding with Functions

Since version 3.0, Spring Cloud Stream supports a functional programming
model natively. This means that the only requirement for turning your
application into a sink is presence of a `java.util.function.Consumer`
bean in the application context.

    @Bean
    public Consumer<UserMessage> logUserMessage() {
      return userMessage -> {
        // process message
      }
    };

A source application is one where a `Supplier` bean is present. It can
return an object, in which case Spring Cloud Stream will invoke the
supplier repeatedly. Alternatively, the function can return a reactive
stream, which will be used as is.

    @Bean
    Supplier<Flux<UserMessage>> generateUserMessages() {
      return () -> /* flux creation logic */;
    }

A processor application works similarly to a source application, except
it is triggered by presence of a `Function` bean.

### Sample

Sample applications are available:

  - For [streaming input, functional
    style](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-pubsub-stream-functional-sample).

[1mdiff --git a/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubMessageChannelBinderEmulatorIntegrationTests.java b/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubMessageChannelBinderEmulatorIntegrationTests.java[m
[1mindex 7538cfeb2..8315ba8eb 100644[m
[1m--- a/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubMessageChannelBinderEmulatorIntegrationTests.java[m
[1m+++ b/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubMessageChannelBinderEmulatorIntegrationTests.java[m
[36m@@ -48,7 +48,7 @@[m [mpublic class PubSubMessageChannelBinderEmulatorIntegrationTests extends[m
 [m
 	@Override[m
 	protected PubSubTestBinder getBinder() {[m
[31m-		return new PubSubTestBinder(emulator.getEmulatorHostPort());[m
[32m+[m		[32mreturn new PubSubTestBinder(emulator.getEmulatorHostPort(), this.applicationContext);[m
 	}[m
 [m
 	@Override[m
[1mdiff --git a/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubTestBinder.java b/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubTestBinder.java[m
[1mindex b2606f726..0a72b2de8 100644[m
[1m--- a/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubTestBinder.java[m
[1m+++ b/spring-cloud-gcp-pubsub-stream-binder/src/test/java/com/google/cloud/spring/stream/binder/pubsub/PubSubTestBinder.java[m
[36m@@ -42,6 +42,7 @@[m [mimport io.grpc.ManagedChannelBuilder;[m
 import org.springframework.cloud.stream.binder.AbstractTestBinder;[m
 import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;[m
 import org.springframework.cloud.stream.binder.ExtendedProducerProperties;[m
[32m+[m[32mimport org.springframework.context.ApplicationContext;[m
 import org.springframework.context.support.GenericApplicationContext;[m
 [m
 /**[m
[36m@@ -54,7 +55,7 @@[m [mpublic class PubSubTestBinder extends AbstractTestBinder<PubSubMessageChannelBin[m
 		ExtendedConsumerProperties<PubSubConsumerProperties>,[m
 		ExtendedProducerProperties<PubSubProducerProperties>> {[m
 [m
[31m-	public PubSubTestBinder(String host) {[m
[32m+[m	[32mpublic PubSubTestBinder(String host, ApplicationContext applicationContext) {[m
 		GcpProjectIdProvider projectIdProvider = () -> "porto sentido";[m
 [m
 		// Transport channel provider so that test binder talks to emulator.[m
[36m@@ -103,8 +104,8 @@[m [mpublic class PubSubTestBinder extends AbstractTestBinder<PubSubMessageChannelBin[m
 		PubSubMessageChannelBinder binder =[m
 				new PubSubMessageChannelBinder(null, pubSubChannelProvisioner, pubSubTemplate,[m
 						new PubSubExtendedBindingProperties());[m
[31m-		GenericApplicationContext context = new GenericApplicationContext();[m
[31m-		binder.setApplicationContext(context);[m
[32m+[m
[32m+[m		[32mbinder.setApplicationContext(applicationContext);[m
 		this.setBinder(binder);[m
 	}[m
 [m

## Migration Guide from Spring Framework on Google Cloud 1.x to 2.x

### Maven Group ID Change

Spring Cloud
[unbundled](https://spring.io/blog/2019/07/24/simplifying-the-spring-cloud-release-train)
Spring Framework on Google Cloud and other cloud providers from their release train. To
use the newly unbundled libraries, add the
`spring-cloud-gcp-dependencies` bill of materials (BOM) and change the
`spring-cloud-gcp` group IDs in your `pom.xml` files:

**Before (pom.xml).**

``` xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>${spring-cloud.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter</artifactId>
  </dependency>
</dependencies>
```

**After (pom.xml).**

``` xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId> 
      <artifactId>spring-cloud-gcp-dependencies</artifactId>
      <version>3.4.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.google.cloud</groupId> 
    <artifactId>spring-cloud-gcp-starter</artifactId>
  </dependency>
  ... 
</dependencies>
```

  - Upgrade to Spring Cloud Ilford release

  - Explicitly add `spring-cloud-gcp-dependencies` to import the BOM

  - Change Group IDs to `com.google.cloud`

  - Add other `starters` as desired (i.e.,
    `spring-cloud-gcp-starter-pubsub` or
    `spring-cloud-gcp-starter-storage`)

### Java Package Name Change

All code in Spring Framework on Google Cloud has been moved from
`org.springframework.cloud.gcp` over to the `com.google.cloud.spring`
package.

### Deprecated Items Removed

  - Property `spring.cloud.gcp.datastore.emulatorHost`  
    Use `spring.cloud.gcp.datastore.host` instead

  - `GcpPubSubHeaders.ACKNOWLEDGEMENT`  
    Use `GcpPubSubHeaders.ORIGINAL_MESSAGE`, which is of type
    `BasicAcknowledgeablePubsubMessage`

  - `SpannerQueryOptions.getQueryOptions()`  
    Use `getOptions()`

  - `PubSubTemplate.subscribe(String, MessageReceiver)`  
    Use `subscribe(String, Consumer<BasicAcknowledgeablePubsubMessage>)`

  - `SpannerReadOptions.getReadOptions()`  
    Use `getOptions()`

  - Cloud Logging
    
      - `org.springframework.cloud.gcp.autoconfigure.logging` package  
        Use `com.google.cloud.spring.logging` from the
        `spring-cloud-gcp-logging` module.
    
      - Cloud Logging Logback Appenders  
        Replace
        `org/springframework/cloud/gcp/autoconfigure/logging/logback[-json]-appender.xml`
        with
        `com/google/cloud/spring/logging/logback[-json]-appender.xml`
        from the `spring-cloud-gcp-logging` module.
        
        **logback-spring.xml.**
        
        ``` xml
        <configuration>
          <include resource="com/google/cloud/spring/logging/logback-appender.xml" />
          ...
        </configuration>
        ```

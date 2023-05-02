# Spring Boot Starters for Google Client Libraries

Please see below for a list of additional preview starters provided, with dependencies and auto-configurations to work with corresponding Google Client Libraries.

These starters are not included in the Spring Framework on Google Cloud Bill of Materials (BOM), 
and should be explicitly added as dependencies. For example, to use the starter for Cloud Natural Language, 
add the following dependency to your `pom.xml`.

```xml
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-language-spring-starter</artifactId>
    <version>{starters-version}-preview</version>
  </dependency>
```

| Client Library | Starter Maven Artifact |
|----------------| -----------------------|

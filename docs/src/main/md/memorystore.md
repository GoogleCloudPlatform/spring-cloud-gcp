## Cloud Memorystore for Redis

### Spring Caching

[Cloud Memorystore for Redis](https://cloud.google.com/memorystore/)
provides a fully managed in-memory data store service. Cloud Memorystore
is compatible with the Redis protocol, allowing easy integration with
[Spring
Caching](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html).

All you have to do is create a Cloud Memorystore instance and use its IP
address in `application.properties` file as `spring.redis.host` property
value. Everything else is exactly the same as setting up redis-backed
Spring caching.

<div class="note">

Memorystore instances and your application instances have to be located
in the same region.

</div>

In short, the following dependencies are needed:

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

And then you can use `org.springframework.cache.annotation.Cacheable`
annotation for methods you’d like to be cached.

``` java
@Cacheable("cache1")
public String hello(@PathVariable String name) {
    ....
}
```

If you are interested in a detailed how-to guide, please check [Spring
Boot Caching using Cloud Memorystore
codelab](https://codelabs.developers.google.com/codelabs/cloud-spring-cache-memorystore/).

Cloud Memorystore documentation can be found
[here](https://cloud.google.com/memorystore/docs/redis/).

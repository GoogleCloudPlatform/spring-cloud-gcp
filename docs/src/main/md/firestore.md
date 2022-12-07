## Spring Data Cloud Firestore

<div class="note">

Currently some features are not supported: query by example,
projections, and auditing.

</div>

[Spring Data](https://projects.spring.io/spring-data/) is an abstraction
for storing and retrieving POJOs in numerous storage technologies.
Spring Framework on Google Cloud adds Spring Data Reactive Repositories support for
[Google Cloud Firestore](https://cloud.google.com/firestore/) in native
mode, providing reactive template and repositories support. To begin
using this library, add the `spring-cloud-gcp-data-firestore` artifact
to your project.

Maven coordinates for this module only,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>spring-cloud-gcp-data-firestore</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
      implementation("com.google.cloud:spring-cloud-gcp-data-firestore")
    }

We provide a Spring Boot Starter for Spring Data Firestore, with which
you can use our recommended auto-configuration setup. To use the
starter, see the coordinates below.

``` xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>spring-cloud-gcp-starter-data-firestore</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
      implementation("com.google.cloud:spring-cloud-gcp-starter-data-firestore")
    }

### Configuration

#### Properties

The Spring Boot starter for Google Cloud Firestore provides the
following configuration options:

|                                                      |                                                                                                                                                                                                                 |          |                                                                              |
| ---------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ---------------------------------------------------------------------------- |
| Name                                                 | Description                                                                                                                                                                                                     | Required | Default value                                                                |
| `spring.cloud.gcp.firestore.enabled`                 | Enables or disables Firestore auto-configuration                                                                                                                                                                | No       | `true`                                                                       |
| `spring.cloud.gcp.firestore.project-id`              | Google Cloud project ID where the Google Cloud Firestore API is hosted, if different from the one in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                                                          | No       |                                                                              |
| `spring.cloud.gcp.firestore.emulator.enabled`        | Enables the usage of an emulator. If this is set to true, then you should set the `spring.cloud.gcp.firestore.host-port` to the host:port of your locally running emulator instance                             | No       | `false`                                                                      |
| `spring.cloud.gcp.firestore.host-port`               | The host and port of the Firestore service; can be overridden to specify connecting to an already-running [Firestore emulator](https://firebase.google.com/docs/emulator-suite/install_and_configure) instance. | No       | `firestore.googleapis.com:443` (the host/port of official Firestore service) |
| `spring.cloud.gcp.firestore.credentials.location`    | OAuth2 credentials for authenticating with the Google Cloud Firestore API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                                             | No       |                                                                              |
| `spring.cloud.gcp.firestore.credentials.encoded-key` | Base64-encoded OAuth2 credentials for authenticating with the Google Cloud Firestore API, if different from the ones in the [Spring Framework on Google Cloud Core Module](#spring-framework-on-google-cloud-core)                              | No       |                                                                              |
| `spring.cloud.gcp.firestore.credentials.scopes`      | [OAuth2 scope](https://developers.google.com/identity/protocols/googlescopes) for Spring Framework on Google CloudFirestore credentials                                                                                  | No       | <https://www.googleapis.com/auth/datastore>                                  |

#### Supported types

You may use the following field types when defining your persistent
entities or when binding query parameters:

  - `Long`

  - `Integer`

  - `Double`

  - `Float`

  - `String`

  - `Boolean`

  - `Character`

  - `Date`

  - `Map`

  - `List`

  - `Enum`

  - `com.google.cloud.Timestamp`

  - `com.google.cloud.firestore.GeoPoint`

  - `com.google.cloud.firestore.Blob`

#### Reactive Repository settings

Spring Data Repositories can be configured via the
`@EnableReactiveFirestoreRepositories` annotation on your main
`@Configuration` class. With our Spring Boot Starter for Spring Data
Cloud Firestore, `@EnableReactiveFirestoreRepositories` is automatically
added. It is not required to add it to any other class, unless there is
a need to override finer grain configuration parameters provided by
[`@EnableReactiveFirestoreRepositories`](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/blob/main/spring-cloud-gcp-data-firestore/src/main/java/com/google/cloud/spring/data/firestore/repository/config/EnableReactiveFirestoreRepositories.java).

#### Autoconfiguration

Our Spring Boot autoconfiguration creates the following beans available
in the Spring application context:

  - an instance of `FirestoreTemplate`

  - instances of all user defined repositories extending
    `FirestoreReactiveRepository` (an extension of
    `ReactiveCrudRepository` with additional Cloud Firestore features)
    when repositories are enabled

  - an instance of
    [`Firestore`](https://developers.google.com/resources/api-libraries/documentation/firestore/v1/java/latest/)
    from the Google Cloud Java Client for Firestore, for convenience and
    lower level API access

### Object Mapping

Spring Data Cloud Firestore allows you to map domain POJOs to [Cloud
Firestore
collections](https://firebase.google.com/docs/firestore/data-model#collections)
and documents via annotations:

``` java
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "usersCollection")
public class User {
  /** Used to test @PropertyName annotation on a field. */
  @PropertyName("drink")
  public String favoriteDrink;

  @DocumentId private String name;

  private Integer age;

  public User() {}

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return this.age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
```

`@Document(collectionName = "usersCollection")` annotation configures
the collection name for the documents of this type. This annotation is
optional, by default the collection name is derived from the class name.

`@DocumentId` annotation marks a field to be used as document id. This
annotation is required and the annotated field can only be of `String`
type.

<div class="note">

If the property annotated with `@DocumentId` is `null` the document id
is generated automatically when the entity is saved.

</div>

<div class="note">

Internally we use Firestore client library object mapping. See [the
documentation](https://developers.google.com/android/reference/com/google/firebase/firestore/package-summary)
for supported annotations.

</div>

#### Embedded entities and lists

Spring Data Cloud Firestore supports embedded properties of custom types
and lists. Given a custom POJO definition, you can have properties of
this type or lists of this type in your entities. They are stored as
embedded documents (or arrays, correspondingly) in the Cloud Firestore.

Example:

``` java
@Document(collectionName = "usersCollection")
public class User {
  /** Used to test @PropertyName annotation on a field. */
  @PropertyName("drink")
  public String favoriteDrink;

  @DocumentId private String name;

  private Integer age;

  private List<String> pets;

  private List<Address> addresses;

  private Address homeAddress;

  public List<String> getPets() {
    return this.pets;
  }

  public void setPets(List<String> pets) {
    this.pets = pets;
  }

  public List<Address> getAddresses() {
    return this.addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

  public Timestamp getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Timestamp updateTime) {
    this.updateTime = updateTime;
  }

  @PropertyName("address")
  public Address getHomeAddress() {
    return this.homeAddress;
  }

  @PropertyName("address")
  public void setHomeAddress(Address homeAddress) {
    this.homeAddress = homeAddress;
  }
  public static class Address {
    String streetAddress;
    String country;

    public Address() {}
  }
}
```

### Reactive Repositories

[Spring Data
Repositories](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/reactive/ReactiveCrudRepository.html)
is an abstraction that can reduce boilerplate code.

For example:

``` java
public interface UserRepository extends FirestoreReactiveRepository<User> {
  Flux<User> findBy(Pageable pageable);

  Flux<User> findByAge(Integer age);

  Flux<User> findByAge(Integer age, Sort sort);

  Flux<User> findByAgeOrderByNameDesc(Integer age);

  Flux<User> findAllByOrderByAge();

  Flux<User> findByAgeNot(Integer age);

  Flux<User> findByNameAndAge(String name, Integer age);

  Flux<User> findByHomeAddressCountry(String country);

  Flux<User> findByFavoriteDrink(String drink);

  Flux<User> findByAgeGreaterThanAndAgeLessThan(Integer age1, Integer age2);

  Flux<User> findByAgeGreaterThan(Integer age);

  Flux<User> findByAgeGreaterThan(Integer age, Sort sort);

  Flux<User> findByAgeGreaterThan(Integer age, Pageable pageable);

  Flux<User> findByAgeIn(List<Integer> ages);

  Flux<User> findByAgeNotIn(List<Integer> ages);

  Flux<User> findByAgeAndPetsContains(Integer age, List<String> pets);

  Flux<User> findByNameAndPetsContains(String name, List<String> pets);

  Flux<User> findByPetsContains(List<String> pets);

  Flux<User> findByPetsContainsAndAgeIn(String pets, List<Integer> ages);

  Mono<Long> countByAgeIsGreaterThan(Integer age);
}
```

Spring Data generates a working implementation of the specified
interface, which can be autowired into an application.

The `User` type parameter to `FirestoreReactiveRepository` refers to the
underlying domain type.

<div class="note">

You can refer to nested fields using [Spring Data JPA Property
Expressions](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-property-expressions)

</div>

``` java
public class MyApplication {

  @Autowired UserRepository userRepository;

  void writeReadDeleteTest() {
    List<User.Address> addresses =
        Arrays.asList(
            new User.Address("123 Alice st", "US"), new User.Address("1 Alice ave", "US"));
    User.Address homeAddress = new User.Address("10 Alice blvd", "UK");
    User alice = new User("Alice", 29, null, addresses, homeAddress);
    User bob = new User("Bob", 60);

    this.userRepository.save(alice).block();
    this.userRepository.save(bob).block();

    assertThat(this.userRepository.count().block()).isEqualTo(2);
    assertThat(this.userRepository.findAll().map(User::getName).collectList().block())
        .containsExactlyInAnyOrder("Alice", "Bob");

    User aliceLoaded = this.userRepository.findById("Alice").block();
    assertThat(aliceLoaded.getAddresses()).isEqualTo(addresses);
    assertThat(aliceLoaded.getHomeAddress()).isEqualTo(homeAddress);

    // cast to SimpleFirestoreReactiveRepository for method be reachable with Spring Boot 2.4
    SimpleFirestoreReactiveRepository repository =
        AopTestUtils.getTargetObject(this.userRepository);
    StepVerifier.create(
            repository
                .deleteAllById(Arrays.asList("Alice", "Bob"))
                .then(this.userRepository.count()))
        .expectNext(0L)
        .verifyComplete();
  }
}
```

Repositories allow you to define custom Query Methods (detailed in the
following sections) for retrieving and counting based on filtering and
paging parameters.

<div class="note">

Custom queries with `@Query` annotation are not supported since there is
no query language in Cloud Firestore

</div>

### Firestore Operations & Template

`FirestoreOperations` and its implementation, `FirestoreTemplate`,
provides the Template pattern familiar to Spring developers.

Using the auto-configuration provided by Spring Data Cloud Firestore,
your Spring application context will contain a fully configured
`FirestoreTemplate` object that you can autowire in your application:

``` java
@SpringBootApplication
public class FirestoreTemplateExample {

    @Autowired
    FirestoreOperations firestoreOperations;

    public Mono<User> createUsers() {
        return this.firestoreOperations.save(new User("Alice", 29))
            .then(this.firestoreOperatons.save(new User("Bob", 60)));
    }

    public Flux<User> findUsers() {
        return this.firestoreOperations.findAll(User.class);
    }

    public Mono<Long> removeAllUsers() {
        return this.firestoreOperations.deleteAll(User.class);
    }
}
```

The Template API provides support for:

  - Read and write operations

  - [Transactions](#_transactions)

  - [Subcollections](#_subcollections) operations

### Query methods by convention

``` java
public class MyApplication {
  void partTreeRepositoryMethodTest() {
    User u1 = new User("Cloud", 22, null, null, new Address("1 First st., NYC", "USA"));
    u1.favoriteDrink = "tea";
    User u2 =
        new User(
            "Squall",
            17,
            Arrays.asList("cat", "dog"),
            null,
            new Address("2 Second st., London", "UK"));
    u2.favoriteDrink = "wine";
    Flux<User> users = Flux.fromArray(new User[] {u1, u2});

    this.userRepository.saveAll(users).blockLast();

    assertThat(this.userRepository.count().block()).isEqualTo(2);
    assertThat(this.userRepository.findBy(PageRequest.of(0, 10)).collectList().block())
        .containsExactly(u1, u2);
    assertThat(this.userRepository.findByAge(22).collectList().block()).containsExactly(u1);
    assertThat(this.userRepository.findByAgeNot(22).collectList().block()).containsExactly(u2);
    assertThat(this.userRepository.findByHomeAddressCountry("USA").collectList().block())
        .containsExactly(u1);
    assertThat(this.userRepository.findByFavoriteDrink("wine").collectList().block())
        .containsExactly(u2);
    assertThat(this.userRepository.findByAgeGreaterThanAndAgeLessThan(20, 30).collectList().block())
        .containsExactly(u1);
    assertThat(this.userRepository.findByAgeGreaterThan(10).collectList().block())
        .containsExactlyInAnyOrder(u1, u2);
    assertThat(this.userRepository.findByNameAndAge("Cloud", 22).collectList().block())
        .containsExactly(u1);
    assertThat(
            this.userRepository
                .findByNameAndPetsContains("Squall", Collections.singletonList("cat"))
                .collectList()
                .block())
        .containsExactly(u2);
  }
}
```

In the example above the query method implementations in
`UserRepository` are generated based on the name of the methods using
the [Spring Data Query creation naming
convention](https://docs.spring.io/spring-data/data-commons/docs/current/reference/html#repositories.query-methods.query-creation).

Cloud Firestore only supports filter components joined by AND, and the
following operations:

  - `equals`

  - `is not equal`

  - `greater than or equals`

  - `greater than`

  - `less than or equals`

  - `less than`

  - `is null`

  - `contains` (accepts a `List` with up to 10 elements, or a singular
    value)

  - `in` (accepts a `List` with up to 10 elements)

  - `not in` (accepts a `List` with up to 10 elements)

<div class="note">

If `in` operation is used in combination with `contains` operation, the
argument to `contains` operation has to be a singular value.

</div>

After writing a custom repository interface specifying just the
signatures of these methods, implementations are generated for you and
can be used with an auto-wired instance of the repository.

### Transactions

Read-only and read-write transactions are provided by
`TransactionalOperator` (see this [blog
post](https://spring.io/blog/2019/05/16/reactive-transactions-with-spring)
on reactive transactions for details). In order to use it, you would
need to autowire `ReactiveFirestoreTransactionManager` like this:

``` java
public class MyApplication {
  @Autowired ReactiveFirestoreTransactionManager txManager;
}
```

After that you will be able to use it to create an instance of
`TransactionalOperator`. Note that you can switch between read-only and
read-write transactions using `TransactionDefinition` object:

``` java
DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
transactionDefinition.setReadOnly(false);
TransactionalOperator operator =
    TransactionalOperator.create(this.txManager, transactionDefinition);
```

When you have an instance of `TransactionalOperator`, you can invoke a
sequence of Firestore operations in a transaction by using
`operator::transactional`:

``` java
User alice = new User("Alice", 29);
User bob = new User("Bob", 60);

this.userRepository
    .save(alice)
    .then(this.userRepository.save(bob))
    .as(operator::transactional)
    .block();

this.userRepository
    .findAll()
    .flatMap(
        a -> {
          a.setAge(a.getAge() - 1);
          return this.userRepository.save(a);
        })
    .as(operator::transactional)
    .collectList()
    .block();

assertThat(this.userRepository.findAll().map(User::getAge).collectList().block())
    .containsExactlyInAnyOrder(28, 59);
```

<div class="note">

Read operations in a transaction can only happen before write
operations. All write operations are applied atomically. Read documents
are locked until the transaction finishes with a commit or a rollback,
which are handled by Spring Data. If an `Exception` is thrown within a
transaction, the rollback operation is performed. Otherwise, the commit
operation is performed.

</div>

#### Declarative Transactions with @Transactional Annotation

This feature requires a bean of `SpannerTransactionManager`, which is
provided when using `spring-cloud-gcp-starter-data-firestore`.

`FirestoreTemplate` and `FirestoreReactiveRepository` support running
methods with the `@Transactional`
[annotation](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction-declarative)
as transactions. If a method annotated with `@Transactional` calls
another method also annotated, then both methods will work within the
same transaction.

One way to use this feature is illustrated here. You would need to do
the following:

1.  Annotate your configuration class with the
    `@EnableTransactionManagement` annotation.

2.  Create a service class that has methods annotated with
    `@Transactional`:

<!-- end list -->

``` java
class UserService {
  @Autowired private UserRepository userRepository;

  @Transactional
  public Mono<Void> updateUsers() {
    return this.userRepository
        .findAll()
        .flatMap(
            a -> {
              a.setAge(a.getAge() - 1);
              return this.userRepository.save(a);
            })
        .then();
  }
}
```

1.  Make a Spring Bean provider that creates an instance of that class:

<!-- end list -->

``` java
@Bean
public UserService userService() {
  return new UserService();
}
```

After that, you can autowire your service like so:

``` java
public class MyApplication {
  @Autowired UserService userService;
}
```

Now when you call the methods annotated with `@Transactional` on your
service object, a transaction will be automatically started. If an error
occurs during the execution of a method annotated with `@Transactional`,
the transaction will be rolled back. If no error occurs, the transaction
will be committed.

### Subcollections

A subcollection is a collection associated with a specific entity.
Documents in subcollections can contain subcollections as well, allowing
you to further nest data. You can nest data up to 100 levels deep.

<div class="warning">

Deleting a document does not delete its subcollections\!

</div>

To use subcollections you will need to create a
`FirestoreReactiveOperations` object with a parent entity using
`FirestoreReactiveOperations.withParent` call. You can use this object
to save, query and remove entities associated with this parent. The
parent doesn’t have to exist in Firestore, but should have a non-empty
id field.

Autowire `FirestoreReactiveOperations`:

``` java
@Autowired
FirestoreReactiveOperations firestoreTemplate;
```

Then you can use this object to create a `FirestoreReactiveOperations`
object with a custom parent:

``` java
FirestoreReactiveOperations bobTemplate =
    this.firestoreTemplate.withParent(new User("Bob", 60));

PhoneNumber phoneNumber = new PhoneNumber("111-222-333");
bobTemplate.save(phoneNumber).block();
assertThat(bobTemplate.findAll(PhoneNumber.class).collectList().block())
    .containsExactly(phoneNumber);
bobTemplate.deleteAll(PhoneNumber.class).block();
assertThat(bobTemplate.findAll(PhoneNumber.class).collectList().block()).isEmpty();
```

### Update Time and Optimistic Locking

Firestore stores update time for every document. If you would like to
retrieve it, you can add a field of `com.google.cloud.Timestamp` type to
your entity and annotate it with `@UpdateTime` annotation.

``` java
@UpdateTime
Timestamp updateTime;
```

#### Using update time for optimistic locking

A field annotated with `@UpdateTime` can be used for optimistic locking.
To enable that, you need to set `version` parameter to `true`:

``` java
@UpdateTime(version = true)
Timestamp updateTime;
```

When you enable optimistic locking, a precondition will be automatically
added to the write request to ensure that the document you are updating
was not changed since your last read. It uses this field’s value as a
document version and checks that the version of the document you write
is the same as the one you’ve read.

If the field is empty, a precondition would check that the document with
the same id does not exist to ensure you don’t overwrite existing
documents unintentionally.

### Cloud Firestore Spring Boot Starter

If you prefer using Firestore client only, Spring Framework on Google Cloud provides a
convenience starter which automatically configures authentication
settings and client objects needed to begin using [Google Cloud
Firestore](https://cloud.google.com/firestore/) in native mode.

See [documentation](https://cloud.google.com/firestore/docs/) to learn
more about Cloud Firestore.

To begin using this library, add the
`spring-cloud-gcp-starter-firestore` artifact to your project.

Maven coordinates,
using [Spring Framework on Google Cloud BOM](getting-started.xml#bill-of-materials):

``` xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-firestore</artifactId>
</dependency>
```

Gradle coordinates:

    dependencies {
      implementation("com.google.cloud:spring-cloud-gcp-starter-firestore")
    }

#### Using Cloud Firestore

The starter automatically configures and registers a `Firestore` bean in
the Spring application context. To start using it, simply use the
`@Autowired` annotation.

``` java
@Autowired
Firestore firestore;

 void writeDocumentFromObject() throws ExecutionException, InterruptedException {
   // Add document data with id "joe" using a custom User class
   User data =
       new User(
           "Joe",
           Arrays.asList(new Phone(12345, PhoneType.CELL), new Phone(54321, PhoneType.WORK)));

   // .get() blocks on response
   WriteResult writeResult = this.firestore.document("users/joe").set(data).get();

   LOGGER.info("Update time: " + writeResult.getUpdateTime());
 }

 User readDocumentToObject() throws ExecutionException, InterruptedException {
   ApiFuture<DocumentSnapshot> documentFuture = this.firestore.document("users/joe").get();

   User user = documentFuture.get().toObject(User.class);

   LOGGER.info("read: " + user);

   return user;
 }
```

### Emulator Usage

The Google Cloud Firebase SDK provides a local, in-memory emulator for
Cloud Firestore, which you can use to develop and test your application.

First follow [the Firebase emulator installation
steps](https://firebase.google.com/docs/emulator-suite/install_and_configure)
to install, configure, and run the emulator.

<div class="note">

By default, the emulator is configured to run on port 8080; you will
need to ensure that the emulator does not run on the same port as your
Spring application.

</div>

Once the Firestore emulator is running, ensure that the following
properties are set in your `application.properties` of your Spring
application:

    spring.cloud.gcp.firestore.emulator.enabled=true
    spring.cloud.gcp.firestore.host-port=${EMULATOR_HOSTPORT}

From this point onward, your application will connect to your locally
running emulator instance instead of the real Firestore service.

### Samples

Spring Framework on Google Cloud provides Firestore sample applications to demonstrate
API usage:

  - [Reactive Firestore Repository sample
    application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-data-firestore-sample):

  - [Firestore Client Library sample
    application](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-firestore-sample)

### Test

`Testcontainers` provides a `gcloud` module which offers `FirestoreEmulatorContainer`. See more at the [docs](https://www.testcontainers.org/modules/gcloud/#firestore)

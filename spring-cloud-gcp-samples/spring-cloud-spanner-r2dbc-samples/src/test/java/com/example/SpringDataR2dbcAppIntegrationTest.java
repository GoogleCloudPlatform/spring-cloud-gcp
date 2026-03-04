package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SpringDataR2dbcAppIntegrationTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("gcp.project", () -> System.getProperty("gcp.project", "spring-cloud-gcp-ci"));
    registry.add("spanner.database", () -> System.getProperty("spanner.database", "trades"));
    registry.add("spanner.instance", () -> System.getProperty("spanner.instance", "spring-demo"));
  }

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

  @Autowired private BookRepository bookRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @AfterEach
  void deleteRecords() {
    this.webTestClient.get().uri("delete-all").exchange().expectStatus().is2xxSuccessful();
  }

  @Test
  void tryBasicRepoMethods() {
    Book newBook = new Book("Call of the wild", null, null);
    bookRepository.save(newBook).block();
    Book newBook2 = new Book("War and Peace", null, null);
    bookRepository.save(newBook2).block();

    StepVerifier.create(bookRepository.findById(newBook.getId()))
        .expectNextCount(1L)
        .verifyComplete();

    StepVerifier.create(bookRepository.findAll()).expectNextCount(2L).verifyComplete();

    StepVerifier.create(bookRepository.count()).expectNext(2L).verifyComplete();
  }

  @Test
  void testBasicWebEndpoints() throws JacksonException {

    // initially empty table
    this.webTestClient
        .get()
        .uri("/list")
        .exchange()
        .expectBody(Book[].class)
        .isEqualTo(new Book[0]);

    Book newBook = new Book("Call of the wild", null, null);
    this.webTestClient
        .post()
        .uri("/add")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(objectMapper.writeValueAsString(newBook)), String.class)
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    AtomicReference<String> id = new AtomicReference<>();
    this.webTestClient
        .get()
        .uri("/list")
        .exchange()
        .expectBody(Book[].class)
        .value(
            books -> {
              assertThat(books).hasSize(1);
              assertThat(books[0].getTitle()).isEqualTo("Call of the wild");
              assertThat(books[0].getCount()).isEqualTo(0);
              id.set(books[0].getId());
            });

    assertThat(id).doesNotHaveValue("");

    this.webTestClient
        .get()
        .uri("/search/" + id.get())
        .exchange()
        .expectBody(Book.class)
        .value(
            book -> {
              assertThat(book.getTitle()).isEqualTo("Call of the wild");
            });

    this.webTestClient
        .post()
        .uri("/increment-count/" + id.get())
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    this.webTestClient
        .get()
        .uri("/search/" + id.get())
        .exchange()
        .expectBody(Book.class)
        .value(
            book -> {
              assertThat(book.getCount()).isEqualTo(1);
            });
  }

  @Test
  void testJsonWebEndpoints() {
    this.webTestClient
        .post()
        .uri("/add")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(
            Mono.just(
                "{\"title\":\"Call of the wild II\",\"extraDetails\":"
                    + "{\"rating\":\"8\",\"series\":\"yes\"},\"review\":null}"),
            String.class)
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    this.webTestClient
        .post()
        .uri("/add")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(
            Mono.just(
                "{\"title\":\"Call of the wild III\",\"extraDetails\":null,"
                    + "\"review\":{\"reviewerId\":\"John\",\"reviewerContent\":\"Good read.\"}}"),
            String.class)
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    this.webTestClient
        .get()
        .uri("/list")
        .exchange()
        .expectBody(Book[].class)
        .value(
            books -> {
              assertThat(books).hasSize(2);
              for (Book book : books) {
                if (book.getTitle().equals("Call of the wild II")) {
                  assertThat(book.getExtraDetails()).containsEntry("rating", "8");
                  assertThat(book.getExtraDetails()).containsEntry("series", "yes");
                }
                if (book.getTitle().equals("Call of the wild III")) {
                  assertThat(book.getReview()).isEqualTo(new Review("John", "Good read."));
                }
              }
            });
  }
}

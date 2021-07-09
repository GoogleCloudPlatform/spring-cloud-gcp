package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.ServiceOptions;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringDataR2dbcAppIntegrationTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("gcp.project", () -> ServiceOptions.getDefaultProjectId());
    registry.add("spanner.database", () -> System.getProperty("spanner.database","testdb"));
    registry.add("spanner.instance", () -> System.getProperty("spanner.instance", "reactivetest"));
  }

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testAllWebEndpoints() {
    
    // initially empty table
    this.webTestClient.get().uri("/list").exchange()
        .expectBody(Book[].class).isEqualTo(new Book[0]);

    this.webTestClient.post().uri("/add").body(Mono.just("Call of the wild"), String.class)
        .exchange().expectStatus().is2xxSuccessful();

    AtomicReference<String> id = new AtomicReference<>();
    this.webTestClient.get().uri("/list").exchange()
        .expectBody(Book[].class).value(books -> {
       assertThat(books).hasSize(1);
       assertThat(books[0].getTitle()).isEqualTo("Call of the wild");
       id.set(books[0].getId());
    });

    assertThat(id).doesNotHaveValue("");

    this.webTestClient.get().uri("/search/"  +id.get()).exchange()
        .expectBody(Book.class).value(book -> {
      assertThat(book.getTitle()).isEqualTo("Call of the wild");
    });
  }

}

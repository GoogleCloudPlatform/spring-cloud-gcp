/*
 * Copyright 2019-2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides HTTP endpoints for manipulating the BOOK table.
 * <ul>
 *   <li>{@code /list} Returns all books in the table (GET).</li>
 *   <li>{@code /add} Adds a new book with a given title and a generated UUID as {@code id} (POST).</li>
 *   <li>{@code /search/\{id\}} Finds a single book by its ID.</li>
 * </ul>
 */
@RestController
public class WebController {

  @Autowired
  private DatabaseClient r2dbcClient;

  @Autowired
  private BookRepository r2dbcRepository;

  @GetMapping("/list")
  public Flux<Book> listBooks() {
    return r2dbcClient.execute("SELECT id, title FROM BOOK")
        .as(Book.class)
        .fetch().all();
  }

  @PostMapping("/add")
  public Mono<Void> addBook(@RequestBody String bookTitle) {
    return r2dbcClient.insert()
        .into("book")
        .value("id", UUID.randomUUID().toString())
        .value("title", bookTitle)
        .then();
  }


  @GetMapping("/search/{id}")
  public Mono<Book> searchBooks(@PathVariable String id) {
    return r2dbcRepository.findById(id);
  }

}

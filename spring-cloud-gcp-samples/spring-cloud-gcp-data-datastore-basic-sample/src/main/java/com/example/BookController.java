/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
  private final BookRepository bookRepository;

  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @PostMapping("/saveBook")
  public String saveBook(@RequestBody Book book) {
    if (book == null) {
      return "The book is invalid";
    }
    this.bookRepository.save(book);
    return "success";
  }

  @GetMapping("/findAllBooks")
  public String findAllBooks() {
    Iterable<Book> books = this.bookRepository.findAll();
    List<Book> bookList = new ArrayList<>();
    books.forEach(bookList::add);
    return books.toString();
  }

  @GetMapping("/findByAuthor")
  public String findByAuthor(@RequestParam("author") String author) {
    List<Book> books = this.bookRepository.findByAuthor(author);
    return books.toString();
  }

  @GetMapping("/findByYearGreaterThan")
  public String findByYearGreaterThan(@RequestParam("year") Optional<Integer> year) {
    List<Book> books = this.bookRepository.findByYearGreaterThan(year.orElse(0));
    return books.toString();
  }

  @GetMapping("/findByAuthorYear")
  public String findByAuthorYear(
      @RequestParam("author") String author,
      @RequestParam("year") Optional<Integer> year) {
    List<Book> books = this.bookRepository.findByAuthorAndYear(author, year.orElse(0));
    return books.toString();
  }

  @DeleteMapping("/removeAllBooks")
  public void removeAllBooks() {
    this.bookRepository.deleteAll();
  }
}

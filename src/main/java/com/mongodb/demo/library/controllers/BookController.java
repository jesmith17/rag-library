package com.mongodb.demo.library.controllers;

import com.mongodb.demo.library.models.Book;
import com.mongodb.demo.library.services.BookService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping(value="books", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping
    private List<Book> searchBooksByTitle(@RequestParam String title){
        return bookService.bookSearch(title);
    }

    @GetMapping(value="{id}")
    public Book getBookById(@PathVariable("id") String id) {
        Book book = bookService.findById(new ObjectId(id));
        return book;
    }



}

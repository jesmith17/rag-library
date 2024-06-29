package com.mongodb.demo.library.services;

import com.mongodb.demo.library.models.Book;
import com.mongodb.demo.library.repository.BookRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepo;


    public List<Book> bookSearch(String title) {

        System.out.println(title);
        return bookRepo.bookSearch(title);
    }

    public Book findById(ObjectId id) {
        return bookRepo.findById(id).get();
    }

    public Book findByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn);
    }




}

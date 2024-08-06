package com.mongodb.demo.library.services;

import com.mongodb.demo.library.models.Book;
import com.mongodb.demo.library.repository.BookRepository;

import org.bson.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.stage;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private MongoTemplate template;


    public List<Book> bookSearch(String title) {

        System.out.println(title);
        return bookRepo.bookSearch(title);
    }

    public Book findById(String id) {
        return bookRepo.findById(id).get();
    }

    public Book findByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn);
    }


    public List<Book> getSimilarBooks(String referenceBookId){

        Book referenceBook = bookRepo.findByBookId(referenceBookId);

        List<Book> similarBooks = bookRepo.findSimilarBooks(referenceBook.getEmbeddings());

        return similarBooks;
    }


    private List<Book> vectorQuery(double[] embeddings) {


        BsonDocument searchQuery = new BsonDocument();
        BsonArray embeddingsArray = new BsonArray();
        for(double vector: embeddings){
            embeddingsArray.add(new BsonDouble(vector));
        }
        searchQuery.append("queryVector", embeddingsArray);
        searchQuery.append("path",new BsonString("embeddings"));
        searchQuery.append("numCandidates", new BsonInt32(10));
        searchQuery.append("index", new BsonString("default"));
        searchQuery.append("limit", new BsonInt32(10));


        Aggregation agg = newAggregation(
                stage(new BsonDocument().append("$vectorSearch", searchQuery))
        );

        AggregationResults<Book> aggResult = template.aggregate(agg, "books", Book.class);
        List<Book> results = aggResult.getMappedResults();


        return results;
    }





}

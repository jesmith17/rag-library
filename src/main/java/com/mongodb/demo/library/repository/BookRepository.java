package com.mongodb.demo.library.repository;

import com.mongodb.demo.library.models.Book;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
public interface BookRepository extends MongoRepository<Book, String> {




        @Aggregation(pipeline = {"{'$search':{'index': 'default', 'text': {'query': ?0,'path': 'title'}}}"})
        List<Book> bookSearch(String title);

        @Query("{ 'isbn' : ?0 }")
        Book findByIsbn(String id);


        @Query("{ '_id' : ?0 }")
        Book findByBookId(String id);

        @Aggregation(pipeline = {"{'$vectorSearch': { 'queryVector': ?0, 'path': 'embeddings', 'numCandidates': 100, 'index': 'vector_index', 'limit': 10}}" })
        List<Book> findSimilarBooks(float[] embeddings);




    }



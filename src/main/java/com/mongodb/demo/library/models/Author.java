package com.mongodb.demo.library.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("authors")
public class Author {

    @BsonId
    @Id
    private ObjectId id;
    private String name;
    private String sanitizedName;
    private String[] aliases;
    private String[] bookIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSanitizedName() {
        return sanitizedName;
    }

    public void setSanitizedName(String sanitizedName) {
        this.sanitizedName = sanitizedName;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String[] getBookIds() {
        return bookIds;
    }

    public void setBookIds(String[] bookIds) {
        this.bookIds = bookIds;
    }
}

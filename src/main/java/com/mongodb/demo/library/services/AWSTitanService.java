package com.mongodb.demo.library.services;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.mongodb.demo.library.models.Book;
import org.bson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.stage;

@Service
public class AWSTitanService implements AIService {


    private BedrockRuntimeClient runtime;

    @Autowired
    private MongoTemplate template;

    public AWSTitanService(){
        runtime = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

    }


    public double[] generateEmbeddings(String prompt){

        JSONObject jsonBody = new JSONObject()
                .put("inputText", prompt);
        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );
        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.titan-embed-text-v1")
                .contentType("application/json")
                .body(body)
                .build();

        InvokeModelResponse response = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                response.body().asString(StandardCharsets.UTF_8)
        );
        JSONArray embeddings = jsonObject.getJSONArray("embedding");

        double[] embeddingsArray = new double[embeddings.length()];
        for(int i=0; i<embeddings.length(); i++){
            embeddingsArray[i] = embeddings.getDouble(i);
        }
        return embeddingsArray;

    }


    public List<Book> generateResponse(String prompt) {


        // Generate embeddings for prompt
        double[] embeddingsArray = this.generateEmbeddings(prompt);

        // Lookup context in Mongo based on vector search
        List<Book> contextEntries = this.vectorQuery(embeddingsArray);

        // If available books count is less than 10, get some more recommendations


        // Pass prompt to LLM to do query
        StringBuilder builder = new StringBuilder();




        // Always have these 2, the ones above are for the context found in the DB lookup
        builder.append(" \nBased on the above context, ");
        builder.append(prompt);

        JSONObject configObject = new JSONObject()
                .put("maxTokenCount", 4096)
                .put("stopSequences", new JSONArray())
                .put("temperature", 1)
                .put("topP", 1);

        JSONObject jsonBody = new JSONObject()
                .put("inputText", builder.toString())
                .put("textGenerationConfig", configObject);


        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.titan-text-express-v1")
                .contentType("application/json")
                .body(body)
                .build();

        InvokeModelResponse response = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                response.body().asString(StandardCharsets.UTF_8)
        );

        String completion = jsonObject.getJSONArray("results").getJSONObject(0).getString("outputText");


        return null;
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

package org.example.brein;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.Business;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.example.ElasticsearchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.ElasticsearchImpl.timestampPipeline;
public class BreinApiLibraryImpl {
    private static final Logger logger;


    static {
        logger = LoggerFactory.getLogger(BreinApiLibraryImpl.class);
    }

    public static JsonObject readData(String filePath) {
        final JsonReader reader;
        final JsonObject object;

        String file = BreinApiLibraryImpl.class.getResource(filePath).getPath();

        try {
            reader = Json.createReader(new FileReader(file));
            object = reader.readObject();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public List<Business> readJson(URL fileName) throws IOException {

        JsonNode jsonObject = new JacksonJsonpMapper().objectMapper().readValue(fileName, JsonNode.class);

        JsonNode hitsHitsNode = jsonObject.get("hits").get("hits");

        List<JsonNode> listOfSourceNodes = new ArrayList<>();

        for (JsonNode sourceNode : hitsHitsNode) {

            if(sourceNode.get("_source") != null) {

                listOfSourceNodes.add(sourceNode.get("_source"));
            }
        }

        return Arrays.stream(new JacksonJsonpMapper().objectMapper().readValue(listOfSourceNodes.toString(), Business[].class)).toList();
    }

    private Set<String> setOfBusinessIds;

    public int ingest(List<Business> businesses, String index) throws IOException {
        setOfBusinessIds = new HashSet<>();
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Business business : businesses) {

            if (setOfBusinessIds.add(business.id())) {

                br.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .id(index + "-" + business.id())
                                .document(business)
                                .pipeline(timestampPipeline)
                        )
                );
            }
        }

        BulkResponse result = ElasticsearchImpl.bulkRequest(br);

        // Log errors, if any
        if (result.errors()) {
            // log errors, test request line
            logger.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    logger.error(item.error().reason());
                }
            }
        }

        return result.items().size();
    }


}
package org.example;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.stewseo.lowlevel.restclient.PrintUtils;
import io.github.stewseo.yelp.fusion.client.Elasticsearch;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ElasticsearchImpl {
    private static final Elasticsearch elasticsearch;
    protected static ElasticsearchTransport transport;
    public static final String timestampPipeline = "timestamp-pipeline";

    static {
        String host = "1ff0acb6626441789a7e846726159410.us-east-2.aws.elastic-cloud.com";

        int port = 443;
        String scheme = "https";
        String apiKey = System.getenv("API_KEY_ID");
        String apiKeySecret = System.getenv("API_KEY_SECRET");

        elasticsearch = Elasticsearch.getInstance();

        JacksonJsonpMapper mapper = new JacksonJsonpMapper();

        RestClient restClient = elasticsearch.createRestClient(host, port, scheme, apiKey, apiKeySecret);

        transport = elasticsearch.createTransport(restClient, mapper);
    }
    public static void createSynchronousBlockingClient() {
        Elasticsearch.getInstance().createESClient(transport);
    }

    public static void createASynchronousClient() {
        Elasticsearch.getInstance().createESAsyncClient(transport);
    }

    public static void shutdown() {
        Elasticsearch.getInstance().client().shutdown();
    }

    static List<StringTermsBucket> termsAggregationByCategory;

    private static List<StringTermsBucket> getCategories() {
        return elasticsearch.getStringTermsBuckets();
    }

    public static BulkResponse bulkRequest(BulkRequest.Builder br) throws IOException {
        return elasticsearch.client().bulk(br.build());
    }

//        map.remove("restaurants");
//        map.remove("bars");
//        map.remove("nightlife");
//        map.remove("food");
//        map.remove("newamerican");

//        categoriesMap = map.entrySet().stream().sorted(comparingInt(e -> e.getValue().size()))
//                .collect(toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (a, b) -> {
//                            throw new AssertionError();
//                        },
//                        LinkedHashMap::new
//                ));
    }



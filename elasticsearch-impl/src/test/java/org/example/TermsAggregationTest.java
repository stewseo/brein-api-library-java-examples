package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import io.github.stewseo.lowlevel.restclient.PrintUtils;
import io.github.stewseo.yelp.fusion.client.Elasticsearch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TermsAggregationTest {

    private final static Logger logger = LoggerFactory.getLogger(TermsAggregationTest.class);

    @Test
    void termsAggregationTest() throws Exception {

        // Dynamically build each unique bucket: all alias
        TermsAggregation termsAggregation = TermsAggregation.of(t -> t
                .field("all.alias.keyword")
                .size(315)
        );

        // match all documents containing the queryName
        Query matchAll = MatchAllQuery.of(m -> m
                .queryName("location")
        )._toQuery();

        ElasticsearchImpl.createSynchronousBlockingClient();

        ElasticsearchClient esClient = Elasticsearch.getInstance().client();

        SearchResponse<Void> response =  esClient.search(b -> b
                        .index("yelp-fusion-businesses-restaurants-nyc")
                        .size(0) // Set the number of matching documents to zero
                        .query(matchAll) // Set the query that will filter the businesses on which to run the aggregation (all contain all)
                        .aggregations("all-aggs", a -> a // Create an aggregation named "all-aggs"
                                .terms(termsAggregation) // Select the terms aggregation variant.

                        ),
                Void.class // Using Void will ignore any document in the response.
        );

        List<StringTermsBucket> buckets = response.aggregations()
                .get("all-aggs")
                .sterms()
                .buckets().array();

        for (StringTermsBucket bucket: buckets) {
            System.out.println(PrintUtils.cyan("category: " + bucket.key().stringValue() +
                                    ", "+PrintUtils.green("documents: " + bucket.docCount()
                            )
                    )
            );
        }

    }
}

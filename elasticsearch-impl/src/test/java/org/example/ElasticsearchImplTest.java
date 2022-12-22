package org.example;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ExpandWildcard;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.cat.NodesResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.elasticsearch.ingest.PutPipelineResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.DateTime;
import io.github.stewseo.yelp.fusion.client.Elasticsearch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

// test api implementations https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api.html
public class ElasticsearchImplTest {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchImpl.class);
    private static ElasticsearchClient esClient;

    @BeforeAll
    static void beforeAll() {
        ElasticsearchImpl.createSynchronousBlockingClient();

        esClient = Elasticsearch.getInstance().client();
    }

    @AfterAll
    static void afterAll() {
        ElasticsearchImpl.shutdown();
    }

    String testIndex = "test-index";
    String index = "yelp-fusion-businesses-restaurants-nyc";

    @Test
    public void testBuildTime() throws Exception {
        DateTime buildTime = esClient.info().version().buildDate();
        Instant version8_6 = DateTimeFormatter.ISO_INSTANT.parse("2022-12-05T18:22:22.226119655Z", Instant::from);

        assertThat(version8_6).isBefore(buildTime.toInstant());
    }

    @Test
    public void createIndexTest() throws Exception {
        assertThat(esClient.ping()).isEqualTo(5);

        // ElasticsearchIndicesClient, CreateIndexRequest, CreateIndexResponse
        final CreateIndexResponse createResponse = esClient.indices().create(b -> b.index(testIndex));
        assertThat(createResponse.acknowledged()).isTrue();
        assertThat(createResponse.shardsAcknowledged()).isTrue();

        ElasticsearchAsyncClient asyncClient = Elasticsearch.getInstance().asyncClient();

        CompletableFuture<GetIndexResponse> futureResponse = asyncClient.indices().get(b -> b.index(testIndex));

        GetIndexResponse response = futureResponse.get(10, TimeUnit.SECONDS);

        Map<String, IndexState> indices = response.result();

        assertEquals(5, indices.size());

        assertThat(indices.get(testIndex)).isNotNull();
    }

    @Test
    public void indexMappingTest() throws IOException {
        GetMappingResponse mappingResponse = esClient.indices().getMapping(c -> c
                .index(index));

        assertThat(mappingResponse.toString()).isEqualTo("GetMappingResponse: " +
                "{\"yelp-fusion-businesses-restaurants-nyc\":{\"mappings\":{" +
                "\"properties\":{" +
                "\"hours\":{\"type\":\"object\"," +
                    "\"properties\":{" +
                        "\"_open_now\":{\"type\":\"boolean\"}," +
                        "\"is_open_now\":{\"type\":\"boolean\"}," +
                        "\"hours_type\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"," +
                        "\"ignore_above\":256}}}," +
                        "\"open\":{\"type\":\"object\"," +
                            "\"properties\":{\"is_overnight\":{\"type\":\"boolean\"},\"start\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"end\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"day\":{\"type\":\"long\"}}}}},\"distance\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"image_url\":{\"type\":\"text\"," +
                "\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"coordinates\":{\"type\":\"object\"," +
                    "\"properties\":{\"latitude\":{\"type\":\"float\"},\"longitude\":{\"type\":\"float\"}}}," +
                "\"rating\":{\"type\":\"float\"}," +
                "\"review_count\":{\"type\":\"long\"}," +
                "\"transactions\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"photos\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"special_hours\":{\"type\":\"object\",\"properties\":{\"date\":{\"type\":\"date\"},\"is_overnight\":{\"type\":\"boolean\"},\"start\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"end\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"is_closed\":{\"type\":\"boolean\"}}},\"url\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"is_claimed\":{\"type\":\"boolean\"},\"messaging\":{\"type\":\"object\",\"properties\":{\"url\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"use_case_text\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}},\"open_now\":{\"type\":\"boolean\"},\"display_phone\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"phone\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"price\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"alias\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"location\":{\"type\":\"object\",\"properties\":{" +
                    "\"country\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                    "\"cross_streets\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                    "\"address3\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                    "\"address2\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                    "\"city\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                    "\"address1\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"display_address\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"state\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"zip_code\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}},\"categories\":{\"type\":\"object\",\"properties\":{\"alias\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"title\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}," +
                "\"id\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                "\"open_at\":{\"type\":\"float\"}," +
                "\"is_closed\":{\"type\":\"boolean\"}," +
                "\"timestamp\":{\"type\":\"date\"}}}}}");
    }

    @Test
    public void catIndicesTest() throws IOException {

        IndicesResponse indices = esClient.cat().indices(i -> i
                .expandWildcards(ExpandWildcard.All));

        assertThat(indices.valueBody().size()).isEqualTo(77);

        IndicesRecord indicesRecord = indices.valueBody().stream().filter(Objects::nonNull)
                .filter(m -> m.index().equals("yelp-fusion-businesses-restaurants-nyc"))
                .findAny().orElse(null);

        assertThat(indicesRecord).isNotNull();

        assertThat(indices.valueBody().stream().filter(Objects::nonNull)
                .filter(m -> m.index().equals("yelp-fusion-businesses-restaurants-nyc"))
                .count())
                .isEqualTo(1);


        assertThat(indicesRecord.docsCount()).isEqualTo("26927");

        // Cat requests should have the "format=json" added by the transport
        NodesResponse nodes = esClient.cat().nodes(_0 -> _0);

        assertThat(nodes.valueBody().size()).isEqualTo(1);
        assertThat(nodes.valueBody().get(0).master()).isEqualTo("*");
    }

    @Test
    public void createPipelineTest() throws IOException {
        String timestampPipeline = "timestamp-pipeline";

        if (Elasticsearch.getInstance().client()
                .ingest().getPipeline(pipeline -> pipeline
                        .id(timestampPipeline))
                .result().isEmpty())
        {


            PutPipelineResponse response = Elasticsearch.getInstance().client().ingest().putPipeline(p -> p
                    .id(timestampPipeline)
                    .description("add timestamps to docs")
                    .processors(pr -> pr
                            .set(s -> s
                                    .field("_source.timestamp")
                                    .value(JsonData.of("{{_ingest.timestamp}}"))
                            ))
            );

            assertThat(response.acknowledged()).isTrue();

        } else {
            logger.info(timestampPipeline + " already exists");
        }
    }
}
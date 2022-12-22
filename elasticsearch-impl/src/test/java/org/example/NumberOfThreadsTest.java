package org.example;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.Test;

/**
 * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/_number_of_threads.html#_number_of_threads">...</a>
 */
public class NumberOfThreadsTest {

    // The Apache Http Async Client starts by default one dispatcher thread, and a number of worker threads used by the connection manager,
    // as many as the number of locally detected processors (depending on what Runtime.getRuntime().availableProcessors() returns).
    // The number of threads can be modified as follows:

    RestClientBuilder builder = RestClient.builder(
                    new HttpHost("localhost", 9200))
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                        HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setDefaultIOReactorConfig(
                            IOReactorConfig.custom()
                                    .setIoThreadCount(1)
                                    .build());
                }
            });

    @Test
    void numberOfThreadsTest() {
        ElasticsearchImpl.createSynchronousBlockingClient();

    }

}

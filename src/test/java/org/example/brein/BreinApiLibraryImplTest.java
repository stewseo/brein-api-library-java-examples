package org.example.brein;

import com.brein.api.Breinify;
import com.brein.domain.BreinConfig;
import com.brein.engine.BreinEngineType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.ElasticsearchImpl;
import org.example.business.Business;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static junit.framework.TestCase.assertEquals;

public class BreinApiLibraryImplTest {

    @Test
    public void unirestEngineTest() {

        final BreinConfig config = new BreinConfig("938D-3120-64DD-413F-BB55-6573-90CE-473A", "utakxp7sm6weo5gvk7cytw==")
                .setRestEngineType(BreinEngineType.UNIREST_ENGINE);

        Breinify.setConfig(config);

        assertEquals(config.getApiKey(), "938D-3120-64DD-413F-BB55-6573-90CE-473A");

        Breinify.shutdown();
    }

    @Test
    public void unirestJerseyEngineTest() {

        final BreinConfig config = new BreinConfig("938D-3120-64DD-413F-BB55-6573-90CE-473A", "utakxp7sm6weo5gvk7cytw==")
                .setRestEngineType(BreinEngineType.JERSEY_ENGINE);

        Breinify.setConfig(config);

        assertEquals(config.getApiKey(), "938D-3120-64DD-413F-BB55-6573-90CE-473A");

        Breinify.shutdown();
    }

    public static List<CompletableFuture<String>> get(List<URI> uris, String apiKey) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();

        List<HttpRequest> requests = uris.stream()
                .map(HttpRequest::newBuilder)
                .peek(e -> e.setHeader("Authorization", "Bearer " + apiKey))
                .map(HttpRequest.Builder::build)
                .toList();


        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        return requests.stream().map(request -> client
                .sendAsync(request, bodyHandler)
                .whenComplete((r, t) -> System.out.println("status code: "  + r.statusCode()))
                .thenApply(HttpResponse::body)).toList();
    }

    private static List<Business> businesses;
    private static final String index = "yelp-fusion-businesses-restaurants-nyc";

    @Test
    public void readJson() throws IOException {
        BreinApiLibraryImpl impl = new BreinApiLibraryImpl();
        List<Business> businesses = new ArrayList<>();

        for(String path :  List.of("0-8000.json", "8000-15000.json", "15000-22000.json", "22000-28000.json")) {

            URL url = BreinApiLibraryImplTest.class.getResource(path);

            businesses.addAll(impl.readJson(url));
        }
        assertEquals(businesses.size(), 27345);
    }

    @Test
    public void ingestDataTest() throws IOException {
        businesses = new ArrayList<>();
        BreinApiLibraryImpl impl = new BreinApiLibraryImpl();

        for(String path :  List.of("0-8000.json", "8000-15000.json", "15000-22000.json", "22000-28000.json")) {

            URL url = BreinApiLibraryImplTest.class.getResource(path);

            businesses.addAll(new BreinApiLibraryImpl().readJson(url));
        }

        ElasticsearchImpl.createSynchronousBlockingClient();
        int totalIngested = impl.ingest(businesses, index);

        System.out.println("total documents ingested: " + totalIngested);
    }

}
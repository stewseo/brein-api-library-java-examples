package org.example.brein;

import com.brein.api.BreinActivity;
import com.brein.api.BreinTemporalData;
import com.brein.api.Breinify;
import com.brein.domain.BreinConfig;
import com.brein.domain.BreinUser;
import com.brein.domain.results.BreinTemporalDataResult;
import com.brein.engine.BreinEngineType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.stewseo.yelp.fusion.client.yelpfusion.YelpFusionAsyncClient;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.Business;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.Hours;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.details.BusinessDetailsResponse;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.search.SearchBusiness;
import io.github.stewseo.yelp.fusion.client.yelpfusion.business.search.SearchBusinessResponse;
import junit.framework.Assert;
import org.example.ElasticsearchImpl;

import org.junit.Before;
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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    @Test
    public void temporalDataTest() throws Exception {

        Breinify.setConfig("938D-3120-64DD-413F-BB55-6573-90CE-473A", "utakxp7sm6weo5gvk7cytw==");

        final BreinTemporalDataResult result = new BreinTemporalData()
                .setLocation("San Francisco", "California", "USA")
                .execute();

        assertEquals(result.getLocation().getCity(), "San Francisco");
        assertEquals(result.getLocation().getState(), "CA");

        assertEquals(result.getLocation().getGranularity(), "city");

        Double latitude = result.getLocation().getLat();
        Double longitude = result.getLocation().getLon();

        YelpFusionAsyncClient asyncClient = YelpFusionAsyncClient.createAsyncClient(System.getenv("YELP_API_KEY"));

        CompletableFuture<SearchBusinessResponse> response = asyncClient.businesses().search(s -> s
                        .coordinates(c -> c
                                .latitude(latitude)
                                .longitude(longitude))
                        .term("restaurants")
                        .categories(cat -> cat
                                .alias("pizza"))
                        .limit(50)
                        .sort_by("review_count"),
                SearchBusiness.class);

        assertEquals(response.get().businesses().size(), 50);
        assertEquals(response.get().businesses().get(0).name(), "Brenda's French Soul Food");
        assertEquals(response.get().businesses().get(0).rating(), 4.0);
        String id = response.get().businesses().get(0).id();
        assertEquals(id, "lJAGnYzku5zSaLnQ_T6_GQ");

        CompletableFuture<BusinessDetailsResponse> future = asyncClient.businesses().businessDetails(b -> b.id(id));

        Business business = future.get().result().stream().findAny().orElse(null);
        assertNotNull(business);

        Hours businessHours = Objects.requireNonNull(business.hours()).get(0);

        assertEquals(businessHours.hours_type(), "REGULAR");
//        assertTrue(businessHours.is_open_now());

        assertEquals(businessHours.open().size(), 6); // day of week

        assertEquals(business.location().toString(), "" +
                "Location: {\"address1\":\"652 Polk St\"," +
                "\"address2\":\"\"," +
                "\"address3\":\"\"," +
                "\"city\":\"San Francisco\"," +
                "\"zip_code\":\"94102\"," +
                "\"country\":\"US\"," +
                "\"display_address\":[\"652 Polk St\"," +
                "\"San Francisco, CA 94102\"]}");

        assertEquals(business.coordinates().latitude(), 37.78291531984934);
        assertEquals(business.coordinates().longitude(), -122.41889950001861);
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
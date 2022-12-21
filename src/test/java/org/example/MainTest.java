package org.example;

import com.brein.api.BreinActivity;
import com.brein.api.Breinify;
import com.brein.domain.BreinActivityType;
import com.brein.domain.BreinCategoryType;
import com.brein.domain.BreinConfig;
import com.brein.domain.BreinUser;
import com.brein.engine.BreinEngineType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;


/**
 * tests from brein-api-library-java/test/com/brein/api/ApiTestBase.java
 */
public class MainTest {

    private static final String VALID_API_KEY = "41B2-F48C-156A-409A-B465-317F-A0B4-E0E8";
    private static final String VALID_API_KEY_FOR_SECRET = "CA8A-8D28-3408-45A8-8E20-8474-06C0-8548";
    private static final String VALID_SECRET = "lmcoj4k27hbbszzyiqamhg==";

    /**
     * Contains the Breinify User
     */
    protected final BreinUser user = new BreinUser().setEmail("User.Name@email.com");

    /**
     * Contains the Category
     */
    protected final String category = BreinCategoryType.HOME;

    /**
     * Contains the BreinActivityType
     */
    protected final String activityType = BreinActivityType.LOGIN;

    /**
     * Init part
     */
    @BeforeClass
    public static void init() {

        // set logging on
        final Properties props = System.getProperties();
        props.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
    }

    /**
     * Preparation of test case
     */
    @Before
    public void setUp() {
        Breinify.setConfig(VALID_API_KEY);
    }

    @After
    public void cleanUp() {
        Breinify.shutdown();
    }

    protected void asyncTest(final Consumer<AtomicBoolean> test, final long timeoutInMs) {
        final AtomicBoolean runner = new AtomicBoolean(false);
        test.accept(runner);

        final long start = System.currentTimeMillis();
        while (!runner.get()) {
            if (System.currentTimeMillis() - start > timeoutInMs) {
                fail("asyncTest timed out.");
                break;
            }
        }
    }

    @Test
    public void unirestEngineTest() {
        // expected with no valid api key using the api library artifact as a gradle dependency:
        // Caused by: org.junit.platform.commons.PreconditionViolationException: Cannot create Launcher without at least one TestEngine;
        // consider adding an engine implementation JAR to the classpath

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
    public void testBreinAsyncCallback() {

        final BreinUser user = new BreinUser()
                .setFirstName("User")
                .setLastName("Name")
                .setEmail("user.name@me.com");

        final BreinActivity activity = new BreinActivity()
                .setUser(user)
                .setActivityType("testBreinAsyncCallback")
                .setDescription("Super-Description")
                .setCategory(BreinCategoryType.EDUCATION);

        asyncTest(cb -> Breinify.activity(activity, res -> {
            assertEquals(200, res.getStatus());
            cb.set(true);
        }), 2000);
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

}
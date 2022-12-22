package org.example.brein;

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

}
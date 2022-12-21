package org.example;

import com.brein.api.Breinify;
import com.brein.domain.BreinConfig;
import com.brein.engine.BreinEngineType;

public class Main {

    public static void main(String[] args) {

        final BreinConfig config = new BreinConfig("938D-3120-64DD-413F-BB55-6573-90CE-473A", "utakxp7sm6weo5gvk7cytw==")
                .setRestEngineType(BreinEngineType.UNIREST_ENGINE);

        Breinify.setConfig(config);

    }
}
## Testing a response from the TemporalData API to search for popular Yelp restaurants in SF


```
        Breinify.setConfig("938D-3120-64DD-413F-BB55-6573-90CE-473A", "utakxp7sm6weo5gvk7cytw==");

        final BreinTemporalDataResult result = new BreinTemporalData()
                .setLocation("San Francisco", "California", "USA")
                .execute();

        BreinLocationResult location = result.getLocation();
        assertEquals(location.getCity(), "San Francisco");
        assertEquals(location.getState(), "CA");
        assertEquals(location.getGranularity(), "city");
        
        List<SearchBusiness> businesses = searchBusinessFuture.get().businesses();

        Double latitude = location.getLat();
        Double longitude = location.getLon();
        
        assertEquals(businesses.size(), 50);
        SearchBusiness businessSearchResult = businesses.get(0);
        assertEquals(businessSearchResult.name(), "Brenda's French Soul Food");
        assertEquals(businessSearchResult.rating(), 4.0);
        String id = businessSearchResult.id();
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
```


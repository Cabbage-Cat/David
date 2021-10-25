package com.cosmos.david.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MarketDataClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.binance.com/";

    public void test() {
        restTemplate.
    }

}

package com.cosmos.david.client;

import com.cosmos.david.converter.KlineDtoConverter;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Component
public class MarketDataClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.binance.com";

    private static final String KLINES_URL = "/api/v3/klines";

    // X-MBX-USED-WEIGHT-(intervalNum)(intervalLetter)
    private static final int API_WEIGHT_LIMIT_PER_MIN_BY_IP = 1200;

    // X-SAPI-USED-IP-WEIGHT-1M=<value>
    private static final int SAPI_WEIGHT_LIMIT_PER_MIN_BY_IP = 12000;

    // X-SAPI-USED-UID-WEIGHT-1M=<value>
    private static final int SAPI_WEIGHT_LIMIT_PER_MIN_BY_UID = 12000;

    // X-MBX-ORDER-COUNT-(intervalNum)(intervalLetter)


    public KlineRespDto getKlineResponseDto(@Valid KlineReqDto klineReqDto) throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + KLINES_URL)
                .queryParam("symbol", klineReqDto.getBaseAsset() + klineReqDto.getQuoteAsset())
                .queryParam("interval", klineReqDto.getInterval())
                .queryParam("startTime", klineReqDto.getStartTime().toEpochMilli())
                .queryParam("endTime", klineReqDto.getEndTime().toEpochMilli())
                .queryParam("limit", klineReqDto.getLimit());

        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        String kLineRespString = response.getBody();
        HttpHeaders responseHeaders = response.getHeaders();
        int usedWeightPerMin = Integer.parseInt(responseHeaders.get("x-mbx-used-weight-1m").get(0));

        // TODO
        if (usedWeightPerMin >= API_WEIGHT_LIMIT_PER_MIN_BY_IP * 0.8) {
            // send notifications to alert
        }
        return KlineDtoConverter.cvtToRespDtoFromData(kLineRespString);
    }

}

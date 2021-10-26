package com.cosmos.david.client;

import com.cosmos.david.aspect.WeightLimit;
import com.cosmos.david.converter.KlineDtoConverter;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.exception.SyntaxException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Component
public class MarketDataClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.binance.com";

    private static final String KLINES_URL = "/api/v3/klines";

    @WeightLimit
    public List<KlineRespDto> getKlineResponseDto(@Valid KlineReqDto klineReqDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + KLINES_URL)
                .queryParam("symbol", klineReqDto.getBaseAsset() + klineReqDto.getQuoteAsset())
                .queryParam("interval", klineReqDto.getInterval())
                .queryParam("startTime", klineReqDto.getStartTime().toEpochMilli())
                .queryParam("endTime", klineReqDto.getEndTime().toEpochMilli())
                .queryParam("limit", klineReqDto.getLimit());

        ResponseEntity<String> response = restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        String kLineRespString = response.getBody();
        try {
            return KlineDtoConverter.cvtToRespDtoListFromData(kLineRespString);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new SyntaxException("kLineRespParase", List.of(kLineRespString));
        }
    }

}

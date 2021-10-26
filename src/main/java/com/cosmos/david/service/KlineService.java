package com.cosmos.david.service;

import com.cosmos.david.client.MarketDataClient;
import com.cosmos.david.contant.BasicConstant;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.model.KLine;
import com.cosmos.david.repository.KLineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class KlineService {
    @Autowired
    private MarketDataClient marketDataClient;
    @Autowired
    private KLineRepository kLineRepository;

    public KLine fetchKLineFromBeginToEndWithInterval(String baseAsset, String quoteAsset,
                                                      Instant beginTime, Instant endTime, String interval) {
        if (beginTime.isAfter(endTime)) { return null; }
        long diffMs = Duration.between(beginTime, endTime).toMillis();
        long baseMs = BasicConstant.INTERVAL_TO_MS.get(interval);
        long times = diffMs / baseMs; times++;

        Instant travelInstant = Instant.from(beginTime);
        for (int i = 0; i < times; i++) {

        }
//        KlineReqDto dto = new KlineReqDto(baseAsset, quoteAsset, interval, )
        return null;
    }

    @Async
    public KLine saveKLine(KLine kLine) {
        return kLineRepository.save(kLine);
    }

}

package com.cosmos.david.service;

import com.cosmos.david.client.MarketDataClient;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.model.KLine;
import com.cosmos.david.repository.KLineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KlineService {
    @Autowired
    private MarketDataClient marketDataClient;
    @Autowired
    private KLineRepository kLineRepository;

    public KLine fetchKLineFromKLineReq(KlineReqDto klineReqDto) throws JsonProcessingException {
        KlineRespDto klineResponseDto = marketDataClient.getKlineResponseDto(klineReqDto);
        return new KLine(klineReqDto, klineResponseDto);
    }

    public KLine fetchKLineFromBeginAndEndAndInterval(String baseAsset, String quoteAsset,
                                                      Instant beginTime, Instant endTime, String interval) {
        Instant diff = endTime.minusMillis(beginTime.toEpochMilli());

        KlineReqDto dto = new KlineReqDto(baseAsset, quoteAsset, interval, )
    }
    public KLine saveKLine(KLine kLine) {
        return kLineRepository.save(kLine);
    }

}

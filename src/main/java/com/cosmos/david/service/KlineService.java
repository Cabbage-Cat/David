package com.cosmos.david.service;

import com.cosmos.david.aspect.WeightLimit;
import com.cosmos.david.client.MarketDataClient;
import com.cosmos.david.contant.Assets;
import com.cosmos.david.contant.BasicConstant;
import com.cosmos.david.contant.Interval;
import com.cosmos.david.converter.KlineDtoConverter;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.model.KLine;
import com.cosmos.david.model.KLineFetchTime;
import com.cosmos.david.model.KLineType;
import com.cosmos.david.model.Symbol;
import com.cosmos.david.repository.KLineFetchTimeRepository;
import com.cosmos.david.repository.KLineRepository;
import com.cosmos.david.repository.SymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cosmos.david.contant.BasicConstant.FETCH_KLINE_MAX_LIMIT_PER_REQ;

@Service
public class KlineService {
    @Autowired
    private MarketDataClient marketDataClient;
    @Autowired
    private KLineRepository kLineRepository;
    @Autowired
    private KLineFetchTimeRepository kLineFetchTimeRepository;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private SymbolRepository symbolRepository;

    public void fetchKLineFromBeginToEndWithInterval(String baseAsset, String quoteAsset,
                                                      Instant beginTime, Instant endTime, String interval) {
        if (beginTime.isAfter(endTime) || !Assets.BASE_ASSETS.contains(baseAsset)
        || !Assets.QUOTE_ASSETS.contains(quoteAsset) || !Interval.INTERVALS.contains(interval)) { return; }

        long diffMs = Duration.between(beginTime, endTime).toMillis();
        long baseMs = BasicConstant.INTERVAL_TO_MS.get(interval) * FETCH_KLINE_MAX_LIMIT_PER_REQ;
        long times = diffMs / baseMs; times++;

        Instant travelInstant = Instant.from(beginTime);
        for (int i = 0; i < times; i++) {
            Instant endInstant = travelInstant.plus(baseMs, ChronoUnit.MILLIS);
            KlineReqDto req = new KlineReqDto(baseAsset, quoteAsset, interval, travelInstant, endInstant, FETCH_KLINE_MAX_LIMIT_PER_REQ);
            fetchFromReqThenSave(req);
            travelInstant = endInstant;
        }
    }

    public KLine saveOneKLine(KLine kLine) {
        return kLineRepository.save(kLine);
    }

    public List<KLine> saveKLines(List<KLine> kLines) {
        return kLineRepository.saveAll(kLines);
    }

    @Async
    @WeightLimit
    public void fetchFromReqThenSave(final KlineReqDto reqDto) {
        KLineType kLineType = new KLineType(reqDto.getBaseAsset(), reqDto.getQuoteAsset(), reqDto.getInterval());
        Optional<KLineFetchTime> lastFetchTime = kLineFetchTimeRepository.findById(kLineType);
        if (lastFetchTime.isEmpty() || lastFetchTime.get().getLastFetchTime().isAfter(reqDto.getEndTime())) { return; }
        List<KlineRespDto> klineResponseDto = marketDataClient.getKlineResponseDto(reqDto);
        List<KLine> collect = klineResponseDto.stream()
                .map(respDto -> KlineDtoConverter.cvtFromReqAndResp(reqDto, respDto))
                .collect(Collectors.toList());
        if (collect.isEmpty()) { return; }
        saveKLines(collect);
        Instant saveInstant = reqDto.getEndTime().compareTo(Instant.now()) < 0 ? reqDto.getEndTime() : Instant.now();
        kLineFetchTimeRepository.save(new KLineFetchTime(kLineType, saveInstant));
    }


    public void fetchAllCoinKLinesWithTimeAndInterval(Instant beginTime, Instant endTime, String interval) {
        List<Symbol> allSymbols = symbolRepository.findAll();
        allSymbols.forEach(symbol -> {

        });
    }



}

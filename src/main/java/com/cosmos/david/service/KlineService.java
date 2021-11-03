package com.cosmos.david.service;

import com.cosmos.david.aspect.WeightLimit;
import com.cosmos.david.client.MarketDataClient;
import com.cosmos.david.contant.BasicConstant;
import com.cosmos.david.contant.Interval;
import com.cosmos.david.converter.KlineDtoConverter;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.model.*;
import com.cosmos.david.repository.KLineFetchTimeRepository;
import com.cosmos.david.repository.KLineRepository;
import com.cosmos.david.repository.SymbolRepository;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.cosmos.david.contant.BasicConstant.*;

@Service
public class KlineService implements InitializingBean {
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
    private Set<String> symbols;

    private Map<KLineType, Set<KLine>> kLinesMap = new ConcurrentHashMap<>();

    private Map<KLineType, Triple<Instant, KLine, Boolean>> forkMap = new ConcurrentHashMap<>();


    @Async
    public void fetchKLineFromBeginToEndWithInterval(String symbol,
                                                      Instant beginTime, Instant endTime, String interval) {

        if (beginTime.isAfter(endTime) || !symbols.contains(symbol)
            || !Interval.INTERVALS.contains(interval)) { return; }


        // System.out.println("Fetch " + symbol + " with interval " + interval + " on time:" + beginTime);
        long diffMs = Duration.between(beginTime, endTime).toMillis();
        long baseMs = BasicConstant.INTERVAL_TO_MS.get(interval) * FETCH_KLINE_MAX_LIMIT_PER_REQ;
        long times = diffMs / baseMs; times++;

        Instant travelInstant = Instant.from(beginTime);
        for (int i = 0; i < times; i++) {
            Instant endInstant = travelInstant.plus(baseMs, ChronoUnit.MILLIS);
            KlineReqDto req = new KlineReqDto(symbol, interval, travelInstant, endInstant, FETCH_KLINE_MAX_LIMIT_PER_REQ);
            fetchFromReqThenSave(req);
            travelInstant = endInstant;
        }
    }

    @Transactional
    public KLine saveOneKLine(KLine kLine) {
        return kLineRepository.save(kLine);
    }

    @Transactional
    public List<KLine> saveKLines(List<KLine> kLines) {
        return kLineRepository.saveAll(kLines);
    }


    @WeightLimit
    public void fetchFromReqThenSave(final KlineReqDto reqDto) {

        KLineType kLineType = new KLineType(reqDto.getSymbol(), reqDto.getInterval());
        List<KlineRespDto> klineResponseDto = marketDataClient.getKlineResponseDto(reqDto);
        Instant endTime1 = Instant.now();
        if (!klineResponseDto.isEmpty() &&
                klineResponseDto.get(klineResponseDto.size() - 1).getEndTime().compareTo(endTime1) < 0) {
            endTime1 = klineResponseDto.get(klineResponseDto.size() - 1).getEndTime();
        }

        List<KLine> collect = klineResponseDto.stream()
                .map(respDto -> KlineDtoConverter.cvtFromReqAndResp(reqDto, respDto))
                .collect(Collectors.toList());
        if (collect.isEmpty()) { return; }
        collect.forEach(line -> {
            KLineId id = line.getKLineId();
            KLineType type = new KLineType(id.getSymbol(), id.getInterval());
            if (!kLinesMap.containsKey(type)) {
                kLinesMap.put(type, new HashSet<>());
            }
            Set<KLine> kLines = kLinesMap.get(type);
            if (kLines.contains(line)) {
                kLines.remove(line);
            }
            kLines.add(line);
        });
        saveKLines(collect);
        Instant endTime2 = reqDto.getEndTime();
        Instant saveInstant = endTime2;
        if (endTime1.isBefore(endTime2)) { saveInstant = endTime1; }

        System.out.println("Save " + kLineType + " " + saveInstant);
        kLineFetchTimeRepository.saveAndFlush(new KLineFetchTime(kLineType, saveInstant));
    }


    private void fetchAllCoinKLinesWithTimeAndInterval(Instant beginTime, Instant endTime, String interval) {
        try {
            for (String symbol : symbols) {
                Optional<KLineFetchTime> fetchTime = kLineFetchTimeRepository.findById(new KLineType(symbol, interval));
                Instant beginCpy = Instant.ofEpochMilli(beginTime.toEpochMilli());
                if (fetchTime.isPresent() && fetchTime.get().getLastFetchTime().compareTo(beginCpy) >= 0) {
                    beginCpy = fetchTime.get().getLastFetchTime();
                }

                if (beginCpy.plusMillis(INTERVAL_TO_MS.get(interval)).isAfter(endTime)) { continue; }

                fetchKLineFromBeginToEndWithInterval(symbol, beginCpy, endTime, interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchAllCoinKLinesTillNowWithDurationAndInterval(Duration duration, String interval) {
        Instant endTime = Instant.now();
        Instant beginTime = endTime.minus(duration);
        fetchAllCoinKLinesWithTimeAndInterval(beginTime, endTime, interval);
    }


    public void fetchAllCoinsKWithAllInterval() {
        // 4h, 1d
        List<String> intervals = List.of("4h", "12h", "1d");
        for (String interval : intervals) {
            Runnable task = () -> fetchAllCoinKLinesTillNowWithDurationAndInterval(BasicConstant.DEFAULT_DURATION, interval);
            taskScheduler.scheduleAtFixedRate(task, BasicConstant.INTERVAL_TO_MS.get(interval));
        }
    }


    @Override
    @Transactional
    public void afterPropertiesSet() throws Exception {
        symbols = symbolRepository.findAll().stream()
                .map(Symbol::getSymbol)
                .collect(Collectors.toSet());
        fetchAllCoinsKWithAllInterval();

        Thread.sleep(60000);
        calcTechAllCoinsWithAllInterval();
        Thread.sleep(60000);
        setDailyForkSignalForAllCoinsInSchedule();
        Thread.sleep(60000);


        findRetestSignalForAllCoins();
    }

    private Set<KLine> findAllKLinesBySymbolAndInterval(String symbol, String interval) {
        KLineType type = new KLineType(symbol, interval);
        if (!kLinesMap.containsKey(type)) {
            kLinesMap.put(type, new HashSet<>(kLineRepository.findAllKLinesBySymbolAndInterval(symbol, interval)));
        }
        return kLinesMap.get(type);
    }

    private Deque<KLine> getTBefore(KLineId id, int t) {

        Set<KLine> kLines = findAllKLinesBySymbolAndInterval(id.getSymbol(), id.getInterval());
        Instant startTime = id.getStartTime();
        ArrayDeque<KLine> lines = kLines.stream()
                .filter(line -> line.getStartTime().isBefore(startTime))
                .sorted(Comparator.comparing(KLine::getStartTime)).collect(Collectors.toCollection((Supplier<ArrayDeque<KLine>>) ArrayDeque::new));
        while (lines.size() > t) {
            lines.pollFirst();
        }
        return lines;
    }

    @Transactional
    protected void setKLineEMA(final KLine k) {
        Deque<KLine> beforeLines = getTBefore(k.getKLineId(), 1);
        if (beforeLines.isEmpty()) {
            k.setEma12(k.getEndPrice());
            k.setEma20(k.getEndPrice());
            return;
        }

        KLine first = beforeLines.getFirst();
        double lastEma12 = first.getEma12();
        double lastEma20 = first.getEma20();
        double ema12 = ((lastEma12 * 11) / 13) + ((k.getEndPrice() * 2) / 13);
        double ema20 = ((lastEma20 * 25) / 27) + ((k.getEndPrice() * 2) / 27);
        k.setEma12(ema12);
        k.setEma20(ema20);
    }

    @Transactional
    protected void setBBand(final KLine k) {
        Deque<KLine> linesBefore = getTBefore(k.getKLineId(), 19);
        if (linesBefore.size() < 19) {
            k.setMa20(k.getEndPrice());
            k.setBollUp(k.getEndPrice());
            k.setBollDown(k.getEndPrice());
            return;
        }

        double ma20 = (linesBefore.stream().mapToDouble(KLine::getEndPrice).sum() + k.getEndPrice()) / 20;
        double sigma = Math.sqrt((linesBefore.stream().mapToDouble(line -> (line.getEndPrice() - ma20) * (line.getEndPrice() - ma20)).sum()
                + (k.getEndPrice() - ma20) * (k.getEndPrice() - ma20)) / 20);
        double bollUp = ma20 + 2 * sigma;
        double bollDown = ma20 - 2 * sigma;
        k.setMa20(ma20);
        k.setBollUp(bollUp);
        k.setBollDown(bollDown);
    }

    private boolean isBollModified(final KLine kLine) {
        return Double.compare(kLine.getMa20(), kLine.getEndPrice()) != 0;
    }

    private boolean isEMAModified(final KLine kLine) {
        return Double.compare(kLine.getEma12(), kLine.getEndPrice()) != 0;
    }

    @Transactional
    protected void caculateTech(KLineType type) {
        Deque<KLine> lines = findAllKLinesBySymbolAndInterval(type.getSymbol(), type.getInterval())
                .stream()
                .filter(kLine -> !isBollModified(kLine) || !isEMAModified(kLine))
                .sorted(Comparator.comparing(KLine::getStartTime)).collect(Collectors.toCollection((Supplier<ArrayDeque<KLine>>) ArrayDeque::new));
        if (!lines.isEmpty()) {
            KLine last = lines.getLast();
            if (last.getEndTime().isAfter(Instant.now())) {
                lines.pollLast();
            }
        }
        for (KLine line : lines) {
            setKLineEMA(line);
            setBBand(line);
        }
        saveKLines(lines.stream().toList());
    }

    private void calcTechAllCoinsWithInterval(String interval) {
        symbols.forEach(symbol -> {
            KLineType type = new KLineType(symbol, interval);
            caculateTech(type);
        });
    }

    public void calcTechAllCoinsWithAllInterval() {
        // 4h, 12h, 1d
        List<String> intervals = List.of("4h", "12h", "1d");
        for (String interval : intervals) {
            Runnable task = () -> calcTechAllCoinsWithInterval(interval);
            taskScheduler.scheduleAtFixedRate(task, BasicConstant.INTERVAL_TO_MS.get(interval));
        }
    }

    private KLine findLatestDailyGoldForkSignal(final KLineType type) {
        List<KLine> lines = findAllKLinesBySymbolAndInterval(type.getSymbol(), type.getInterval()).stream()
                .sorted(Comparator.comparing(KLine::getStartTime))
                .collect(Collectors.toList());
        List<KLine> res = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            KLine lastLine = lines.get(i - 1);
            KLine line = lines.get(i);
            if (lastLine.getEma12() < lastLine.getEma20() &&
                line.getEma12() > line.getEma20()) {
                res.add(line);
            }
        }
        if (res.isEmpty()) { return null; }
        return res.get(res.size() - 1);
    }

    private KLine findLatestDailyDeathForkSignal(final KLineType type) {
        List<KLine> lines = findAllKLinesBySymbolAndInterval(type.getSymbol(), type.getInterval()).stream()
                .sorted(Comparator.comparing(KLine::getStartTime))
                .collect(Collectors.toList());
        List<KLine> res = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            KLine lastLine = lines.get(i - 1);
            KLine line = lines.get(i);
            if (lastLine.getEma12() > lastLine.getEma20() &&
                    line.getEma12() < line.getEma20()) {
                res.add(line);
            }
        }
        if (res.isEmpty()) { return null; }
        return res.get(res.size() - 1);
    }

    private void setDailyForkSignal(final KLineType type) {
        KLine gold = findLatestDailyGoldForkSignal(type);
        KLine death = findLatestDailyDeathForkSignal(type);
        if (gold == null || death == null) { return; }
        boolean goldFork = gold.getEndTime().isAfter(death.getEndTime());
        KLine save = goldFork ? gold : death;
        forkMap.put(type, new ImmutableTriple<>(save.getEndTime(), save, goldFork));
    }

    public void setDailyForkSignalForAllCoins() {
        symbols.forEach(symbol -> {
            setDailyForkSignal(new KLineType(symbol, "1d"));
        });
    }

    public void setDailyForkSignalForAllCoinsInSchedule() {
        Runnable task1d = this::setDailyForkSignalForAllCoins;
        taskScheduler.scheduleAtFixedRate(task1d, BasicConstant.INTERVAL_TO_MS.get("1d"));
    }

    private List<KLine> findBBandsRetestMA20ForGoldForkSignal(final KLineType type, final Instant forkTime) {

        Instant validTime = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant t = forkTime.isAfter(validTime) ? forkTime : validTime;
        return findAllKLinesBySymbolAndInterval(type.getSymbol(), type.getInterval()).stream()
                .filter(line -> line.getEndTime().isAfter(t))
                .sorted(Comparator.comparing(KLine::getStartTime))
                .filter(line -> {
                    double bias = line.getEndPrice() * BOLL_RETEST_BIAS_PERCENT;
                    double minPrice = line.getMa20() - bias;
                    double maxPrice = line.getMa20() + bias;
                    return line.getEndPrice() >= minPrice && line.getEndPrice() <= maxPrice
                            && Double.compare(line.getEndPrice(), line.getMa20()) != 0;
                })
                .collect(Collectors.toList());
    }

    private List<KLine> findBBandsRetestMA20ForDeathForkSignal(final KLineType type, final Instant forkTime) {

        Instant validTime = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant t = forkTime.isAfter(validTime) ? forkTime : validTime;
        return findAllKLinesBySymbolAndInterval(type.getSymbol(), type.getInterval()).stream()
                .filter(line -> line.getEndTime().isAfter(t))
                .sorted(Comparator.comparing(KLine::getStartTime))
                .filter(line -> {
                    double bias = line.getEndPrice() * BOLL_RETEST_BIAS_PERCENT;
                    double minPrice = line.getMa20() - bias;
                    double maxPrice = line.getMa20() + bias;
                    return line.getEndPrice() < minPrice && line.getEndPrice() > maxPrice
                            && Double.compare(line.getEndPrice(), line.getMa20()) != 0;
                })
                .collect(Collectors.toList());
    }

    private void getRetestSignalFor4h(final KLineType type) {
        Triple<Instant, KLine, Boolean> triple = forkMap.get(type);
        if (triple == null) { return; }
        Instant forkTime = triple.getLeft();
        KLine forkLine = triple.getMiddle();
        Boolean isGoldFork = triple.getRight();
        List<KLine> retestSignal;
        if (isGoldFork) {
            retestSignal = findBBandsRetestMA20ForGoldForkSignal(new KLineType(type.getSymbol(), "4h"), forkTime);
            if (!retestSignal.isEmpty()) System.out.println("Long");
        } else {
            retestSignal = findBBandsRetestMA20ForDeathForkSignal(new KLineType(type.getSymbol(), "4h"), forkTime);
            if (!retestSignal.isEmpty()) System.out.println("Short");
        }
        retestSignal.forEach(line -> {
            System.out.println(line.getKLineId() + ":" + line.getEndPrice());
        });
    }

    private void getRetestSignalFor4hWithAllCoins() {
        symbols.forEach(symbol -> {
            getRetestSignalFor4h(new KLineType(symbol, "1d"));
        });
    }
    private void getRetestSignalFor12h(final KLineType type) {
        Triple<Instant, KLine, Boolean> triple = forkMap.get(type);
        if (triple == null) { return; }
        Instant forkTime = triple.getLeft();
        KLine forkLine = triple.getMiddle();
        Boolean isGoldFork = triple.getRight();
        List<KLine> retestSignal = new ArrayList<>();
        if (isGoldFork) {
            retestSignal.addAll(findBBandsRetestMA20ForGoldForkSignal(new KLineType(type.getSymbol(), "12h"), forkTime));
            if (!retestSignal.isEmpty()) System.out.println("Long");

        } else {
            retestSignal.addAll(findBBandsRetestMA20ForDeathForkSignal(new KLineType(type.getSymbol(), "12h"), forkTime));
            if (!retestSignal.isEmpty()) System.out.println("Short");
        }
        retestSignal.forEach(System.out::println);
    }

    private void getRetestSignalFor12hWithAllCoins() {
        symbols.forEach(symbol -> {
            getRetestSignalFor12h(new KLineType(symbol, "1d"));
        });
    }

    public void findRetestSignalForAllCoins() {
        Runnable task4h = this::getRetestSignalFor4hWithAllCoins;
        Runnable task12h = this::getRetestSignalFor12hWithAllCoins;
        taskScheduler.scheduleAtFixedRate(task4h, BasicConstant.INTERVAL_TO_MS.get("4h"));
        taskScheduler.scheduleAtFixedRate(task12h, BasicConstant.INTERVAL_TO_MS.get("12h"));
    }

}

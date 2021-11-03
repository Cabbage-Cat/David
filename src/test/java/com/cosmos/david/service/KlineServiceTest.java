package com.cosmos.david.service;


import com.cosmos.david.client.SymbolDataClient;
import com.cosmos.david.converter.Convert24HrPriceJSONToSymbol;
import com.cosmos.david.model.KLine;
import com.cosmos.david.model.KLineType;
import com.cosmos.david.model.Symbol;
import com.cosmos.david.repository.KLineFetchTimeRepository;
import com.cosmos.david.repository.KLineRepository;
import com.cosmos.david.repository.SymbolRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class KlineServiceTest {
    @Autowired
    private KlineService klineService;

    @Autowired
    KLineRepository kLineRepository;

    @Autowired
    SymbolRepository symbolRepository;

    @Autowired
    SymbolDataClient symbolDataClient;

    @Autowired
    KLineFetchTimeRepository kLineFetchTimeRepository;

    @Test
    void test() {
        Instant begin = ZonedDateTime.of(2017, 1, 1, 0, 0,0,0,ZoneOffset.UTC)
                .toInstant();
        Instant end = ZonedDateTime.of(2021, 3, 1, 0, 0,0,0,ZoneOffset.UTC)
                .toInstant();

    }

    @Test
    void test1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Convert24HrPriceJSONToSymbol> convert24HrPriceJSONToSymbols = objectMapper.readValue(new File("/home/cateatcabbage/IdeaProjects/David/src/main/resources/data/24HRPrice/2021-10-24.json")
                , new TypeReference<>() {
                });
        List<Symbol> collect = convert24HrPriceJSONToSymbols.stream()
                .map(symbolConvert -> new Symbol(symbolConvert.getSymbol()))
                .collect(Collectors.toList());
        symbolRepository.saveAll(collect);
    }
    @Test
    void test2() {

    }
}

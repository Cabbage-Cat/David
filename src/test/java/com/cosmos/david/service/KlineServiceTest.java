package com.cosmos.david.service;


import com.cosmos.david.repository.KLineRepository;
import com.cosmos.david.repository.SymbolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class KlineServiceTest {
    @Autowired
    private KlineService klineService;

    @Autowired
    KLineRepository kLineRepository;

    @Autowired
    SymbolRepository symbolRepository;
    @Test
    void test() {
        Instant begin = ZonedDateTime.of(2017, 1, 1, 0, 0,0,0,ZoneOffset.UTC)
                .toInstant();
        Instant end = ZonedDateTime.of(2021, 3, 1, 0, 0,0,0,ZoneOffset.UTC)
                .toInstant();

    }

    void test2() {
        kLineRepository.deleteAll();

    }
}

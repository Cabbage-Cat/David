package com.cosmos.david.service;

import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.model.KLine;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class KlineServiceTest {
    @Autowired
    private KlineService klineService;

    @Test
    void test() throws JsonProcessingException {
        KlineReqDto klineReqDto = new KlineReqDto("BTC", "USDT", "1d",
                Instant.ofEpochMilli(1635091200000L), Instant.ofEpochMilli(1635177600000L),
                50);

        KLine kLine = klineService.fetchKLineFromKLineReq(klineReqDto);
        System.out.println(kLine);
    }
}

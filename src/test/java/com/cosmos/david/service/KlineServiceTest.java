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


}

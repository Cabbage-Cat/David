package com.cosmos.david.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KlineRespDto {
    private Instant startTime;
    private double startPrice;
    private double maxPrice;
    private double minPrice;
    private double endPrice;
    private double tradeVolume;
    private Instant endTime;
    private double tradeMoney;
    private long tradeCount;
    private double buyCount;
    private double buyMoney;
}

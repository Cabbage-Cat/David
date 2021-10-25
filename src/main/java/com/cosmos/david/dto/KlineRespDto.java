package com.cosmos.david.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KlineRespDto {
    private long startTime;
    private double startPrice;
    private double maxPrice;
    private double minPrice;
    private double endPrice;
    private double tradeVolume;
    private long endTime;
    private double tradeMoney;
    private long tradeCount;
    private long buyCount;
    private long buyMoney;
}

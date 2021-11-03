package com.cosmos.david.model;

import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KLine implements Serializable {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private KLineId kLineId;

    private Instant endTime;
    private double startPrice;
    private double endPrice;
    private double maxPrice;
    private double minPrice;
    private double tradeVolume;
    private double tradeMoney;
    private long tradeCount;
    private double buyCount;
    private double buyMoney;
    private double ema12;
    private double ema20;
    private double ma20;
    private double bollUp;
    private double bollDown;

    public Instant getStartTime() {
        return kLineId.getStartTime();
    }

    public KLine(KLineId kLineId, Instant endTime, double startPrice, double endPrice, double maxPrice, double minPrice, double tradeVolume, double tradeMoney, long tradeCount, double buyCount, double buyMoney) {
        this.kLineId = kLineId;
        this.endTime = endTime;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.tradeVolume = tradeVolume;
        this.tradeMoney = tradeMoney;
        this.tradeCount = tradeCount;
        this.buyCount = buyCount;
        this.buyMoney = buyMoney;
    }

}

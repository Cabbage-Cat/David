package com.cosmos.david.model;

import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KLine {

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

    public KLine(KlineReqDto req, KlineRespDto resp) {
        this.kLineId = new KLineId(req.getBaseAsset(), req.getQuoteAsset(), req.getInterval(), resp.getStartTime());
        this.endTime = resp.getEndTime();
        this.startPrice = resp.getStartPrice();
        this.endPrice = resp.getEndPrice();
        this.maxPrice = resp.getMaxPrice();
        this.minPrice = resp.getMinPrice();
        this.tradeVolume = resp.getTradeVolume();
        this.tradeMoney = resp.getTradeMoney();
        this.tradeCount = resp.getTradeCount();
        this.buyCount = resp.getBuyCount();
        this.buyMoney = resp.getBuyMoney();
    }
}

package com.cosmos.david.model;

import lombok.Data;
import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
public class KLine {
    private String baseAsset;
    private String quoteAsset;
    private String interval;

    @Id
    private Instant startTime;
    private Instant endTime;
    private double startPrice;
    private double endPrice;
    private double maxPrice;
    private double minPrice;
    private double tradeVolume;
    private double tradeMoney;
    private long tradeCount;
    private long buyCount;
    private long buyMoney;

}

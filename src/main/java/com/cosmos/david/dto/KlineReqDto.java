package com.cosmos.david.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KlineReqDto {
    @NotNull
    private String symbol = "BTCUSDT";

    @NotNull
    private String interval = "1d";

    private Instant startTime = Instant.now();

    private Instant endTime = Instant.now();

    @Max(1000)
    private int limit = 500;
}

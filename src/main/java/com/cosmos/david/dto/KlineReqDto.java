package com.cosmos.david.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KlineReqDto {
    @NotNull
    private String symbol;
    @NotNull
    private String interval;

    private long startTime;

    private long endTime;

    @Max(1000)
    private int limit;
}

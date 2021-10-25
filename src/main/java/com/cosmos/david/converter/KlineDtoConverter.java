package com.cosmos.david.converter;

import com.cosmos.david.dto.KlineRespDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.Instant;

@Data
public class KlineDtoConverter {

    private static ObjectMapper mapper = new ObjectMapper();

    public static KlineRespDto cvtToRespDtoFromData(String respData) throws JsonProcessingException {
        respData = respData.substring(1, respData.length() - 1);
        Object[] data = mapper.readValue(respData, Object[].class);
        KlineRespDto klineRespDto = new KlineRespDto();
        klineRespDto.setStartTime(Instant.ofEpochMilli((Long) data[0]));
        klineRespDto.setStartPrice(Double.parseDouble((String) data[1]));
        klineRespDto.setMaxPrice(Double.parseDouble((String) data[2]));
        klineRespDto.setMinPrice(Double.parseDouble((String) data[3]));
        klineRespDto.setEndPrice(Double.parseDouble((String) data[4]));
        klineRespDto.setTradeVolume(Double.parseDouble((String) data[5]));
        klineRespDto.setEndTime(Instant.ofEpochMilli((Long) data[6]));
        klineRespDto.setTradeMoney(Double.parseDouble((String) data[7]));
        klineRespDto.setTradeCount(Long.parseLong(data[8].toString()));
        klineRespDto.setBuyCount(Double.parseDouble((String) data[9]));
        klineRespDto.setBuyMoney(Double.parseDouble((String) data[10]));
        return klineRespDto;
    }

}

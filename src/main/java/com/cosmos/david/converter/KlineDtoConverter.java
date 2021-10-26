package com.cosmos.david.converter;

import com.cosmos.david.dto.KlineRespDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class KlineDtoConverter {

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<KlineRespDto> cvtToRespDtoListFromData(String dataList) throws JsonProcessingException {
        Object[] dtos = mapper.readValue(dataList, Object[].class);
        return Arrays.stream(dtos).map(dto -> {
            List data = (List) dto;
            return cvtFromRawObjectArray(data);
        }).collect(Collectors.toList());
    }

    private static KlineRespDto cvtFromRawObjectArray(List lst) {
        Object[] data = lst.toArray();
        KlineRespDto klineRespDto = new KlineRespDto();
        klineRespDto.setStartTime(Instant.ofEpochMilli((Long) lst.get(0)));
        klineRespDto.setStartPrice(Double.parseDouble((String) lst.get(1)));
        klineRespDto.setMaxPrice(Double.parseDouble((String) lst.get(2)));
        klineRespDto.setMinPrice(Double.parseDouble((String) lst.get(3)));
        klineRespDto.setEndPrice(Double.parseDouble((String) lst.get(4)));
        klineRespDto.setTradeVolume(Double.parseDouble((String) lst.get(5)));
        klineRespDto.setEndTime(Instant.ofEpochMilli((Long) lst.get(6)));
        klineRespDto.setTradeMoney(Double.parseDouble((String) lst.get(7)));
        klineRespDto.setTradeCount(Long.parseLong(lst.get(8).toString()));
        klineRespDto.setBuyCount(Double.parseDouble((String) lst.get(9)));
        klineRespDto.setBuyMoney(Double.parseDouble((String) lst.get(10)));
        return klineRespDto;
    }

}

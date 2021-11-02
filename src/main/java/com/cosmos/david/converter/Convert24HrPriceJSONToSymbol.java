package com.cosmos.david.converter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Convert24HrPriceJSONToSymbol {
    private String symbol;
    private String price;
}

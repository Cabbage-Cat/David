package com.cosmos.david.client;

import com.cosmos.david.converter.KlineDtoConverter;
import com.cosmos.david.dto.KlineReqDto;
import com.cosmos.david.dto.KlineRespDto;
import com.cosmos.david.exception.SyntaxException;
import com.cosmos.david.model.Symbol;
import com.cosmos.david.repository.SymbolRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SymbolDataClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SymbolRepository symbolRepository;

    private String URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=3000";
    private String HEADER_NAME_AUTH = "X-CMC_PRO_API_KEY";
    private String HEADER_AUTH = "9efb5707-fce1-4b67-b37f-eefb0bf411b9";

    public void getTopSymbols() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME_AUTH, HEADER_AUTH);
        HttpEntity<String> entity = new HttpEntity(headers);


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseObj = restTemplate.exchange(URL, HttpMethod.GET,entity,Object.class);
        LinkedHashMap body = (LinkedHashMap) responseObj.getBody();
        List<LinkedHashMap> data = (List) body.get("data");
        Set<String> fetchSymbols = data.stream().map(map -> {
            String symbol = (String) map.get("symbol");
            symbol = symbol + "USDT";
            return symbol;
        }).collect(Collectors.toSet());

        Set<String> symbols = symbolRepository.findAll().stream()
                .map(Symbol::getSymbol)
                .collect(Collectors.toSet());

        symbolRepository.deleteAll();

        HashSet<String> cpyFetchSymbols = new HashSet<>(fetchSymbols);
        cpyFetchSymbols.forEach(symbol -> {
            if (!symbols.contains(symbol)) {
                fetchSymbols.remove(symbol);
            }
        });

        List<Symbol> saveCollect = fetchSymbols.stream().map(Symbol::new).collect(Collectors.toList());
        symbolRepository.saveAll(saveCollect);

    }

}

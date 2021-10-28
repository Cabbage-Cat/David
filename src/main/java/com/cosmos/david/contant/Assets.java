package com.cosmos.david.contant;

import com.cosmos.david.model.Symbol;
import com.cosmos.david.repository.SymbolRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Assets implements InitializingBean {

    @Autowired
    private SymbolRepository symbolRepository;

    private Set<String> symbols;

    @Override
    public void afterPropertiesSet() throws Exception {
        symbols = symbolRepository.findAll().stream()
                .map(Symbol::getSymbol)
                .collect(Collectors.toSet());
    }
}

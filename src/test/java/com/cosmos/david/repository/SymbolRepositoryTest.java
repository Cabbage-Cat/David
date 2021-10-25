package com.cosmos.david.repository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SymbolRepositoryTest {
    @Autowired
    private SymbolRepository repository;

    @Test
    void addSymbolsData() throws IOException {
    }
}

package com.rj;

import com.rj.service.SearchApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

@SpringBootTest(classes = SearchStartApp.class)
@WebAppConfiguration
public class SearchTest {
    @Autowired
    private SearchApi searchApi;

    @Test
    public void testCreateIndex() throws IOException {
        searchApi.creatIndex();
    }
}

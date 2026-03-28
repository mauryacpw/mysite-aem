package com.mysite.core.models;

import com.mysite.core.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteModelImplTest {

    @Mock
    private QuoteService quoteService;

    @InjectMocks
    private QuoteModelImpl quoteModel;

    @BeforeEach
    void setUp() {
        quoteModel = new QuoteModelImpl();
        quoteModel.setQuoteService(quoteService);
    }

    private String readJson(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    void getQuotes_returnsList_whenValidJsonArray() throws Exception {

        String json = readJson("modelTest.json");

        when(quoteService.getResponse()).thenReturn(json);

        quoteModel.init();

        List<Object> quotes = quoteModel.getQuotes();

        assertEquals(4, quotes.size());
        assertEquals("one", quotes.get(0));
        assertEquals(123, quotes.get(2));
    }


    @Test
    void getQuotes_empty_whenServiceThrowsJsonException() throws Exception {
        when(quoteService.getResponse()).thenReturn("invalid-json-{");
        assertThrows(RuntimeException.class, () -> quoteModel.init());
        assertTrue(quoteModel.getQuotes().isEmpty());
    }

    @Test
    void getQuotes_empty_whenServiceIsNull() {
        quoteModel.quoteService = null;
        quoteModel.init();
        assertTrue(quoteModel.getQuotes().isEmpty());
    }
}
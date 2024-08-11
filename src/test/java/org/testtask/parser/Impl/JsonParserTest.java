package org.testtask.parser.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testtask.entity.StatisticsObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonParserTest {
    private JsonParser jsonParser;

    @BeforeEach
    void setUp() {
        jsonParser = new JsonParser();
    }

    @Test
    void testParseFileWithValidData() throws IOException, InterruptedException, ExecutionException {
        // Мокаем BufferedReader для возврата контролируемого набора данных
        BufferedReader bufferedReaderMock = mock(BufferedReader.class);
        when(bufferedReaderMock.readLine())
                .thenReturn("{\"group\":\"group1\",\"type\":\"type1\",\"number\":1,\"weight\":100}")
                .thenReturn("{\"group\":\"group1\",\"type\":\"type1\",\"number\":2,\"weight\":200}")
                .thenReturn("{\"group\":\"group2\",\"type\":\"type2\",\"number\":3,\"weight\":150}")
                .thenReturn(null);

            StatisticsObject result = jsonParser.parseFile("src/test/resources/test.json");

            // Проверяем результаты
            assertEquals(new BigInteger("300"), result.getGroupWeights().get("group1"));
            assertEquals(new BigInteger("150"), result.getGroupWeights().get("group2"));
            assertEquals(2, result.getDuplicateCounts().get("group1-type1"));
            assertEquals(1, result.getDuplicateCounts().get("group2-type2"));
            assertEquals(new BigInteger("200"), result.getMaxWeight());
            assertEquals(new BigInteger("100"), result.getMinWeight());

    }

    @Test
    void testParseFileWithEmptyData() throws IOException, InterruptedException, ExecutionException {
        // Мокаем BufferedReader для возврата пустого файла
        BufferedReader bufferedReaderMock = mock(BufferedReader.class);
        when(bufferedReaderMock.readLine()).thenReturn(null);

        StatisticsObject result = jsonParser.parseFile("src/test/resources/empty.json");

        // Проверяем результаты
        assertTrue(result.getGroupWeights().isEmpty());
        assertTrue(result.getDuplicateCounts().isEmpty());
        assertEquals(BigInteger.valueOf(Long.MIN_VALUE), result.getMaxWeight());
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE), result.getMinWeight());

    }

}

package org.testtask.parser.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testtask.entity.DataObject;
import org.testtask.entity.StatisticsObject;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    private CsvParser csvParser;
    private StatisticsObject stats;

    @BeforeEach
    public void setUp() {
        csvParser = new CsvParser();
        stats = new StatisticsObject();
    }

    @Test
    public void testParseFile_ValidFile() throws Exception {
        // Arrange
        String filePath = "src/test/resources/valid_data.csv";
        // Create a file with valid data for testing

        // Act
        StatisticsObject result = csvParser.parseFile(filePath);

        // Assert
        assertNotNull(result);
        assertTrue(result.getGroupWeights().size() > 0);
        assertTrue(result.getDuplicateCounts().size() > 0);
        assertNotEquals(BigInteger.valueOf(Long.MIN_VALUE), result.getMaxWeight());
        assertNotEquals(BigInteger.valueOf(Long.MAX_VALUE), result.getMinWeight());
    }


    @Test
    public void testProcessLine_ValidData() {
        // Arrange
        String line = "GroupA,Type1,67890,12345";
        DataObject dataObject = new DataObject("GroupA", "Type1", new BigInteger("12345"), new BigInteger("67890"));

        // Act
        csvParser.processLine(line, stats);

        // Assert
        assertTrue(stats.getGroupWeights().containsKey("GroupA"));
        assertEquals(new BigInteger("12345"), stats.getGroupWeights().get("GroupA"));
        assertTrue(stats.getDuplicateCounts().containsKey("GroupA-Type1"));
        assertEquals(1, stats.getDuplicateCounts().get("GroupA-Type1"));
        assertEquals(new BigInteger("12345"), stats.getMaxWeight());
        assertEquals(new BigInteger("12345"), stats.getMinWeight());
    }

    @Test
    public void testIsHeader() {
        // Arrange
        String validHeader = "Name,Surname,Weight,Value";
        String invalidHeader = "1,2,3,4";

        // Act & Assert
        assertTrue(csvParser.isHeader(validHeader));
        assertFalse(csvParser.isHeader(invalidHeader));
    }

    @Test
    public void testIsNumeric() {
        // Arrange
        String numericStr = "12345";
        String nonNumericStr = "abcde";

        // Act & Assert
        assertTrue(csvParser.isNumeric(numericStr));
        assertFalse(csvParser.isNumeric(nonNumericStr));
    }
}
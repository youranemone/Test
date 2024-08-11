package org.testtask.processor;

import org.testtask.parser.FileParser;
import org.testtask.parser.Impl.CsvParser;
import org.testtask.parser.Impl.JsonParser;

public class FileProcessor {
    public FileParser getFileProcessor(String filePath) {
        if (filePath.endsWith(".csv")) {
            return new CsvParser();
        } else if (filePath.endsWith(".json")) {
            return new JsonParser();
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + filePath);
        }
    }
}

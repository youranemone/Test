package org.testtask.parser;

import org.testtask.entity.StatisticsObject;

public interface FileParser {
    StatisticsObject parseFile(String filePath);
}

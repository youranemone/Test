package org.testtask.parser.Impl;

import org.testtask.entity.DataObject;
import org.testtask.entity.StatisticsObject;
import org.testtask.parser.FileParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CsvParser implements FileParser {

    private static final int CHUNK_SIZE = 2000; // Размер порции для обработки

    @Override
    public StatisticsObject parseFile(String filePath) {
        StatisticsObject stats = new StatisticsObject();
        ExecutorService executor = Executors.newFixedThreadPool(4); // Пул потоков
        List<Future<Void>> futures = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();

            // Проверка, является ли первая строка заголовком
            if (line != null && isHeader(line)) {
                line = reader.readLine(); // Пропускаем заголовок
            }

            while (line != null) {
                lines.add(line);
                if (lines.size() == CHUNK_SIZE) {
                    futures.add(processChunkAsync(new ArrayList<>(lines), stats, executor));
                    lines.clear();
                }
                line = reader.readLine();
            }

            // Обработка оставшихся строк
            if (!lines.isEmpty()) {
                futures.add(processChunkAsync(new ArrayList<>(lines), stats, executor));
            }

            // Ожидание завершения всех задач
            for (Future<Void> future : futures) {
                future.get(); // Синхронизация всех потоков
            }
        } catch (IOException | InterruptedException | java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return stats;
    }

    private Future<Void> processChunkAsync(List<String> lines, StatisticsObject stats, ExecutorService executor) {
        return executor.submit(() -> {
            lines.forEach(line -> {
                try {
                    processLine(line, stats);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            });
            return null; // Возвращаем null, так как Future<Void> не требует возвращаемого значения
        });
    }

    void processLine(String line, StatisticsObject stats) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            BigInteger weight = new BigInteger(parts[2]);
            BigInteger anotherValue = new BigInteger(parts[3]);
            DataObject data = new DataObject(parts[0], parts[1], weight, anotherValue);
            synchronized (stats) { // Синхронизация добавления в общий объект
                stats.addObject(data);
            }
        } else {
            System.err.println("Invalid line format: " + line);
        }
    }

    public boolean isHeader(String line) {
        String[] parts = line.split(",");
        for (String part : parts) {
            if (isNumeric(part)) {
                return false; // Если есть числовые значения, это не заголовок
            }
        }
        return true; // Если все значения текстовые, это предполагается заголовком
    }

    public boolean isNumeric(String str) {
        try {
            new BigInteger(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

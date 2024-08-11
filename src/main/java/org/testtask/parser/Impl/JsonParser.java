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

public class JsonParser implements FileParser {

    private final int chunkSize = 2000; // Размер порции для обработки

    @Override
    public StatisticsObject parseFile(String filePath) {
        StatisticsObject stats = new StatisticsObject();
        ExecutorService executor = Executors.newFixedThreadPool(4); // Пул потоков
        List<Future<StatisticsObject>> futures = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Читаем весь файл в одну строку
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line.trim());
            }

            // Убираем квадратные скобки вокруг JSON массива
            String jsonArray = fileContent.toString().replaceAll("^\\[|\\]$", "").trim();

            // Разбиваем строку на JSON объекты по разделителю ", {"
            String[] jsonObjects = jsonArray.split("(?<=\\}),\\s*(?=\\{)");

            // Преобразуем строки JSON в список
            List<String> lines = new ArrayList<>();
            for (String jsonObject : jsonObjects) {
                // Добавляем фигурные скобки обратно к каждому объекту
                lines.add("{" + jsonObject.trim() + "}");
            }

            // Обрабатываем порции данных
            for (int i = 0; i < lines.size(); i += chunkSize) {
                List<String> chunk = lines.subList(i, Math.min(i + chunkSize, lines.size()));
                futures.add(processChunkAsync(new ArrayList<>(chunk), stats, executor));
            }

            // Ожидание завершения всех задач
            for (Future<StatisticsObject> future : futures) {
                future.get(); // Синхронизация всех потоков
            }
        } catch (IOException | InterruptedException | java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return stats;
    }

    private Future<StatisticsObject> processChunkAsync(List<String> lines, StatisticsObject stats, ExecutorService executor) {
        return executor.submit(() -> {
            for (String line : lines) {
                DataObject data = parseJson(line);
                synchronized (stats) { // Синхронизированный доступ к общему объекту статистики
                    stats.addObject(data);
                }
            }
            return stats;
        });
    }

    private DataObject parseJson(String json) {
        // Проверяем, не является ли входная строка пустой
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        String group = "", type = "";
        BigInteger number = BigInteger.ZERO, weight = BigInteger.ZERO;

        // Убираем фигурные скобки, квадратные скобки и кавычки
        String cleanedJson = json.replaceAll("[{}\\[\\]\"]", "").trim();

        // Проверяем, не пустая ли строка после очистки
        if (cleanedJson.isEmpty()) {
            return null;
        }

        // Разбиваем строку на части
        String[] parts = cleanedJson.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "group":
                    group = value;
                    break;
                case "type":
                    type = value;
                    break;
                case "number":
                    try {
                        number = new BigInteger(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format: " + value);
                    }
                    break;
                case "weight":
                    try {
                        weight = new BigInteger(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid weight format: " + value);
                    }
                    break;
            }
        }

        // Возвращаем null, если все поля остались пустыми
        if (group.isEmpty() && type.isEmpty() && number.equals(BigInteger.ZERO) && weight.equals(BigInteger.ZERO)) {
            return null;
        }

        return new DataObject(group, type, number, weight);
    }

}

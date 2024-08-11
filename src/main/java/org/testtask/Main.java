package org.testtask;

import org.testtask.entity.StatisticsObject;
import org.testtask.parser.FileParser;
import org.testtask.processor.FileProcessor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileProcessor processor = new FileProcessor();

        while (true){
            System.out.println("\nВведите путь к файлу или 'exit' для выхода: ");
            String input = scanner.nextLine();
            if("exit".equalsIgnoreCase(input)){
                System.out.println("Завершение работы приложения...");
                break;
            }
            try {
                FileParser parser = processor.getFileProcessor(input);
                StatisticsObject stats = parser.parseFile(input);
                stats.printStatistics();
            } catch (Exception e) {
                System.out.println("Ошибка обработки файла: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
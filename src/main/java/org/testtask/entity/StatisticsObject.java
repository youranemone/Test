package org.testtask.entity;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class StatisticsObject {
    private Map<String, BigInteger> groupWeights = new HashMap<>();
    private Map<String, Integer> duplicateCounts = new HashMap<>();
    private BigInteger maxWeight = BigInteger.valueOf(Long.MIN_VALUE);
    private BigInteger minWeight = BigInteger.valueOf(Long.MAX_VALUE);

    public void addObject(DataObject obj) {
        String key = obj.getGroup() + "-" + obj.getType();

        // Обновляем вес группы с использованием BigInteger
        groupWeights.put(obj.getGroup(),
                groupWeights.getOrDefault(obj.getGroup(), BigInteger.ZERO).add(obj.getWeight()));

        // Обновляем количество дубликатов
        duplicateCounts.put(key, duplicateCounts.getOrDefault(key, 0) + 1);

        // Обновляем максимальный и минимальный вес
        BigInteger objWeight = obj.getWeight();
        if (objWeight.compareTo(maxWeight) > 0) {
            maxWeight = objWeight;
        }
        if (objWeight.compareTo(minWeight) < 0) {
            minWeight = objWeight;
        }
    }

    public void printStatistics() {
        System.out.println("Дубликаты:\n");
        for (Map.Entry<String, Integer> entry : duplicateCounts.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }

        System.out.println("\nСуммарный вес (по группам)\n");
        for (Map.Entry<String, BigInteger> entry : groupWeights.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("\nМаксимальный вес :" + maxWeight);
        System.out.println("Минимальный вес :" + minWeight);
    }

    public Map<String, BigInteger> getGroupWeights() {
        return groupWeights;
    }

    public Map<String, Integer> getDuplicateCounts() {
        return duplicateCounts;
    }

    public BigInteger getMaxWeight() {
        return maxWeight;
    }

    public BigInteger getMinWeight() {
        return minWeight;
    }

}

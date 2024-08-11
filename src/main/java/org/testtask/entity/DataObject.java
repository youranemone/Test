package org.testtask.entity;

import java.math.BigInteger;

public class DataObject {

    private String group;
    private String type;
    private BigInteger number;
    private BigInteger weight;

    public DataObject(String group,String type,BigInteger number, BigInteger weight) {
        this.group = group;
        this.type = type;
        this.number = number;
        this.weight = weight;
    }

    public DataObject() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger number) {
        this.number = number;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }
}

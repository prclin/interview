package com.bxtdata.interview.interview.task1.entity;

import java.util.stream.Stream;

public class Tag {
    private String key1;
    private String key2;
    private String key3;
    private String space;
    private String name;

    @Override
    public String toString() {
        return "Tag{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", key3='" + key3 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

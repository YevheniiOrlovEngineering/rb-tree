package com.S1A4;

public class Pair<K extends Comparable<K>, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public boolean contains(K key) {
        return this.key == key;
    }
}

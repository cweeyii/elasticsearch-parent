package com.cweeyii.operation;

import java.io.Serializable;

public class Pair<K,V> implements Serializable {
    private static final long serialVersionUID = 7858266026114749055L;
    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }


    public static Pair<String, Integer> defaultPair(String first) {
        return new Pair<String, Integer>(first, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "[" + first + ", " + second + "]";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }


    public K getFirst() {
        return first;
    }


    public V getSecond() {
        return second;
    }
}

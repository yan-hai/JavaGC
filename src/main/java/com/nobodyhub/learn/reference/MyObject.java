package com.nobodyhub.learn.reference;

public class MyObject {
    private byte[] _1K = new byte[1000];
    private final String name;

    public MyObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.printf("Finalizing: %s%n", name);
    }
}

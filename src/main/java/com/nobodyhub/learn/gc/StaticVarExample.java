package com.nobodyhub.learn.gc;

public class StaticVarExample {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private static StaticVarExample instance;

    public static void main(String[] args) {
        StaticVarExample e = new StaticVarExample();
        e.instance = new StaticVarExample();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}

package com.nobodyhub.learn.gc;

public class ClassVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private static ClassVarExample1 instance;

    public static void main(String[] args) {
        ClassVarExample1 e = new ClassVarExample1();
        e.instance = new ClassVarExample1();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}

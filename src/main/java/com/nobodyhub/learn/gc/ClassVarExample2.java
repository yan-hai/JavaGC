package com.nobodyhub.learn.gc;

public class ClassVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private ClassVarExample2 instance;

    public static void main(String[] args) {
        ClassVarExample2 e = new ClassVarExample2();
        e.instance = new ClassVarExample2();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}

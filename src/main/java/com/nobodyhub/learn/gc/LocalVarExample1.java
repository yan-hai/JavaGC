package com.nobodyhub.learn.gc;

public class LocalVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        LocalVarExample1 e = new LocalVarExample1();
        System.gc();
        System.out.println("GC Completed!");
    }
}

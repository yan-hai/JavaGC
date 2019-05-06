package com.nobodyhub.learn.gc;

public class LocalVarExample1 {
    private int _10MB = 10 * 1024 * 1024;
    private byte[] memory = new byte[_10MB];

    public static void main(String[] args) {
        LocalVarExample1 e = new LocalVarExample1();
        System.gc();
        System.out.println("GC Completed!");
    }
}

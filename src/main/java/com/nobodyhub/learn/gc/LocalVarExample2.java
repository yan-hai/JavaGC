package com.nobodyhub.learn.gc;

public class LocalVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        LocalVarExample2 e = new LocalVarExample2();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}

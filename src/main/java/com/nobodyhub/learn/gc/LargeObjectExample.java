package com.nobodyhub.learn.gc;

public class LargeObjectExample {
    private int _500MB = 500 * 1024 * 1024;
    private byte[] memory = new byte[_500MB];

    public static void main(String[] args) {
        LocalVarExample1 e = new LocalVarExample1();
        System.gc();
        System.out.println("GC Completed!");
    }
}

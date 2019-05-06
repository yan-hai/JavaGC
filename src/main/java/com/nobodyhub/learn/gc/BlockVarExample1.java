package com.nobodyhub.learn.gc;

public class BlockVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        {
            BlockVarExample1 e = new BlockVarExample1();
            System.gc();
            System.out.println("1st GC Completed!");
        }
        System.gc();
        System.out.println("2nd GC Completed!");
    }
}

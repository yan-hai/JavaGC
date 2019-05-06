package com.nobodyhub.learn.gc;

public class BlockVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        {
            BlockVarExample2 e = new BlockVarExample2();
            e = null;
            System.gc();
            System.out.println("1st GC Completed!");
        }
        System.gc();
        System.out.println("2nd GC Completed!");
    }
}

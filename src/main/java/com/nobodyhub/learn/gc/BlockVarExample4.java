package com.nobodyhub.learn.gc;

public class BlockVarExample4 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private final int idx;

    public BlockVarExample4(int idx) {
        this.idx = idx;
    }

    public static void main(String[] args) {
        int i = 1;
        while (i <= 4) {
            BlockVarExample4 e = new BlockVarExample4(i);
            e = null;
            System.gc();
            System.out.println(i++ + " GC Completed!");
        }
        System.out.println("Final GC Started!=======");
        System.gc();
        System.out.println("Final GC Finished!=======");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize: " + idx);
    }
}

package com.nobodyhub.learn.gc.eligible;


public class B {
    private double[] array = new double[100 * 1000];
    private int name;
    private A a;

    public B(int name, A myClassA) {
        this.name = name;
        this.a = myClassA;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalized: " + name);
    }
}

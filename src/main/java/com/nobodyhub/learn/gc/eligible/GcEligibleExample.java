package com.nobodyhub.learn.gc.eligible;

public class GcEligibleExample {

    public static void main(String[] args) {
        A a = new A();

        System.out.println("--- Loop Start ---");
        for (int i = 1; i <= 1000; i++) {
            B b = new B(i, a);
            System.gc();
        }
        System.out.println("--- Loop End ---");
    }
}

package com.nobodyhub.learn.gc.eligible;

import java.util.ArrayList;
import java.util.List;

public class GcNonEligibleExample {

    public static void main(String[] args) {
        A myClassA = new A();
        List<B> bList = new ArrayList<B>();

        System.out.println("--- Loop Start ---");
        for (int i = 1; i <= 1000; i++) {
            B b = new B(i, myClassA);
            bList.add(b);
            System.gc();
        }
        System.out.println("--- Loop End ---");
    }
}

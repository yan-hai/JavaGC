package com.nobodyhub.learn.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceExample {
    public static void main(String[] args) {
        ReferenceQueue<MyObject> queue = new ReferenceQueue<>();

        MyObject obj1 = new MyObject("phantom");
        Reference<MyObject> ref = new PhantomReference<>(obj1, queue);
        System.out.println("Ref#get(): " + ref.get());

        MyObject obj2 = new MyObject("normal");

        obj1 = null;
        obj2 = null;

        System.out.println("-- 1st Check --");
        if (checkObjectGced(ref, queue)) {
            takeAction();
        }

        System.out.println("-- do some memory intensive work --");
        for (int i = 0; i < 10; i++) {
            int[] ints = new int[100000];
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        System.out.println("-- 2nd Check --");
        if (checkObjectGced(ref, queue)) {
            takeAction();
        }
    }

    private static boolean checkObjectGced(Reference<MyObject> ref, ReferenceQueue<MyObject> queue) {
        boolean gc = false;
        System.out.println("-- Checking whether object garbage collection due --");
        Reference<? extends MyObject> polledRef = queue.poll();

        System.out.println("polledRef: " + polledRef);
        System.out.println("Is polledRef same: " + (gc = polledRef == ref));

        if (polledRef != null) {
            System.out.println("ref#get(): " + polledRef.get());
        }
        return gc;
    }

    private static void takeAction() {
        System.out.println("pre-mortem cleanup actions");
    }
}

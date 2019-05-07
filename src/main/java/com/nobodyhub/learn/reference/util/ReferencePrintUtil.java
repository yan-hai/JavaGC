package com.nobodyhub.learn.reference.util;

import com.nobodyhub.learn.reference.MyObject;

import java.lang.ref.Reference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReferencePrintUtil {
    public static void printReferences(List<Reference<MyObject>> references) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(() -> {
            try {
                //sleep a little in case if finalizers are currently running
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("-- printing references --");
            references
                    .forEach(ReferencePrintUtil::printReference);
        });
        ex.shutdown();
    }

    private static void printReference(Reference<MyObject> r) {
        System.out.printf("Reference: %s [%s]%n", r.get(),
                r.getClass().getSimpleName());
    }
}

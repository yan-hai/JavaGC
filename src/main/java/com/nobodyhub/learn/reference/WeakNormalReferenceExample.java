package com.nobodyhub.learn.reference;

import com.nobodyhub.learn.reference.util.ReferencePrintUtil;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class WeakNormalReferenceExample {
    public static void main(String[] args) {
        List<Reference<MyObject>> references = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            MyObject myObject = new MyObject("weak " + i);
            Reference<MyObject> ref = new WeakReference<>(myObject);
            references.add(ref);
            new MyObject("normal " + i);
        });
        ReferencePrintUtil.printReferences(references);
    }
}

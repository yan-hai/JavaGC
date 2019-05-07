package com.nobodyhub.learn.reference;

import com.nobodyhub.learn.reference.util.ReferencePrintUtil;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SoftNormalReferenceExample {
    public static void main(String[] args) {
        List<Reference<MyObject>> references = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            MyObject myObject = new MyObject("soft " + i);
            Reference<MyObject> ref = new SoftReference<>(myObject);
            references.add(ref);
            new MyObject("normal " + i);
        });
        ReferencePrintUtil.printReferences(references);
    }
}

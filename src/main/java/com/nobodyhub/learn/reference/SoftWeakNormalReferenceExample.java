package com.nobodyhub.learn.reference;

import com.nobodyhub.learn.reference.util.ReferencePrintUtil;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SoftWeakNormalReferenceExample {
    public static void main(String[] args) {
        List<Reference<MyObject>> references = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            //soft
            Reference<MyObject> ref = new SoftReference<>(new MyObject("soft " + i));
            references.add(ref);
            // weak
            ref = new WeakReference<>(new MyObject("weak " + i));
            references.add(ref);
            // normal
            new MyObject("normal " + i);
        });
        ReferencePrintUtil.printReferences(references);
    }
}

# Examples for java.lang.ref.Reference<T>

## Reachability
> https://docs.oracle.com/javase/8/docs/api/java/lang/ref/package-summary.html#reachability

Going from strongest to weakest, the different levels of reachability reflect the life cycle of an object. They are operationally defined as follows:
* An object is **strongly** reachable if it can be reached by some thread without traversing any reference objects. A newly-created object is strongly reachable by the thread that created it.
* An object is **softly** reachable if it is not strongly reachable but can be reached by traversing a soft reference.
* An object is **weakly** reachable if it is neither strongly nor softly reachable but can be reached by traversing a weak reference. When the weak references to a weakly-reachable object are cleared, the object becomes eligible for finalization.
* An object is **phantom** reachable if it is neither strongly, softly, nor weakly reachable, it has been finalized, and some phantom reference refers to it.
* Finally, an object is **unreachable**, and therefore eligible for reclamation, when it is not reachable in any of the above ways.


## java.lang.ref.Reference<T>
* SoftReference<T>: Soft reference objects, which are cleared at the discretion of the garbage collector in response to memory demand.
* WeakReference<T>: Weak reference objects, which do not prevent their referents from being made finalizable, finalized, and then reclaimed.
* PhantomReference<T>: Phantom reference objects, which are enqueued after the collector determines that their referents may otherwise be reclaimed.
The package provides a queue to store the references after their reachability changes.
* ReferenceQueue<T>: Reference queues, to which registered reference objects are appended by the garbage collector after the appropriate reachability changes are detected.

## Examples for Soft/Weak Reference
> If the output of following code sample is not as expect, please try to adjust the VM Options and add `-XX:+PrintGCDetails` to target the cause. 

### Soft reference vs Normal reference
We will create **soft** reference and **normal** references in the loop.

> VM Options: -Xmx3m -Xms1m

```java
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
```
This will output:
```log
Finalizing: normal 9
Finalizing: normal 10
Finalizing: normal 6
Finalizing: normal 7
Finalizing: normal 8
Finalizing: normal 1
Finalizing: normal 2
Finalizing: normal 3
Finalizing: normal 4
Finalizing: normal 5
-- printing references --
Reference: soft 1 [SoftReference]
Reference: soft 2 [SoftReference]
Reference: soft 3 [SoftReference]
Reference: soft 4 [SoftReference]
Reference: soft 5 [SoftReference]
Reference: soft 6 [SoftReference]
Reference: soft 7 [SoftReference]
Reference: soft 8 [SoftReference]
Reference: soft 9 [SoftReference]
Reference: soft 10 [SoftReference]
```
In the loop we created multiple **soft** references which is wrapped with `SoftReference` and let the underlying references go unreachable when the current loop ends.
We also created the same object without wrapping in the `SoftReferences`. Those we named it **normal** in this example. 

We kept the **soft** references in a collection so that we can afterward check how many of them will return null on get() call. 
Keeping an instance of Reference in a collection does not create a strong reference of the underlying object. 
If we kept the **normal** objects straight in a collection, then for sure we would be creating strong references. 

### Weak reference vs Normal reference
We will create **weak** reference and **normal** references in the loop.

> VM Options: -Xmx1m -Xms1m

```java
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
```
This will output:
```log
Finalizing: normal 10
Finalizing: weak 10
Finalizing: normal 9
Finalizing: weak 9
Finalizing: normal 8
Finalizing: weak 8
Finalizing: normal 7
Finalizing: weak 3
Finalizing: normal 2
Finalizing: weak 2
Finalizing: normal 1
Finalizing: weak 1
Finalizing: weak 7
Finalizing: normal 6
Finalizing: weak 6
Finalizing: normal 5
Finalizing: weak 5
Finalizing: normal 4
Finalizing: weak 4
Finalizing: normal 3
-- printing references --
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
Reference: null [WeakReference]
```
This time all(or most in some run) of the **weak** references are garbaged-collected as soon as they become unreachable, similar to **normal** objects.

### Soft references vs Weak references
We will create both **soft** and **weak** reference in the loop.

> VM Options: -Xmx3m -Xms1m

```java
public class SoftWeakReferenceExample {
    public static void main(String[] args) {
        List<Reference<MyObject>> references = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Reference<MyObject> ref = new SoftReference<>(new MyObject("soft " + i));
            references.add(ref);
             ref = new WeakReference<>(new MyObject("weak " + i));
            references.add(ref);
        });
        ReferencePrintUtil.printReferences(references);
    }
}
```
This will output:
```log
Finalizing: weak 1
Finalizing: weak 2
Finalizing: weak 3
Finalizing: weak 9
Finalizing: weak 10
Finalizing: weak 6
Finalizing: weak 7
Finalizing: weak 8
Finalizing: weak 4
Finalizing: weak 5
-- printing references --
Reference: soft 1 [SoftReference]
Reference: null [WeakReference]
Reference: soft 2 [SoftReference]
Reference: null [WeakReference]
Reference: soft 3 [SoftReference]
Reference: null [WeakReference]
Reference: soft 4 [SoftReference]
Reference: null [WeakReference]
Reference: soft 5 [SoftReference]
Reference: null [WeakReference]
Reference: soft 6 [SoftReference]
Reference: null [WeakReference]
Reference: soft 7 [SoftReference]
Reference: null [WeakReference]
Reference: soft 8 [SoftReference]
Reference: null [WeakReference]
Reference: soft 9 [SoftReference]
Reference: null [WeakReference]
Reference: soft 10 [SoftReference]
Reference: null [WeakReference]
```
All(could be some) **weak** references were garbage collected but soft references were not garbed collected till the end of the execution.

### Soft references vs Weak references vs Normal references
We will create **soft**, **weak**, and **normal** references in the loop.

> VM Options: -Xmx3m -Xms3m

```java
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
```
This will output:
```log
Finalizing: weak 3
Finalizing: normal 2
Finalizing: weak 2
Finalizing: normal 1
Finalizing: weak 1
Finalizing: weak 6
Finalizing: normal 5
Finalizing: weak 5
Finalizing: normal 4
Finalizing: weak 4
Finalizing: normal 3
Finalizing: normal 8
Finalizing: weak 8
Finalizing: normal 7
Finalizing: weak 7
Finalizing: normal 6
Finalizing: normal 10
Finalizing: weak 10
Finalizing: normal 9
Finalizing: weak 9
-- printing references --
Reference: soft 1 [SoftReference]
Reference: null [WeakReference]
Reference: soft 2 [SoftReference]
Reference: null [WeakReference]
Reference: soft 3 [SoftReference]
Reference: null [WeakReference]
Reference: soft 4 [SoftReference]
Reference: null [WeakReference]
Reference: soft 5 [SoftReference]
Reference: null [WeakReference]
Reference: soft 6 [SoftReference]
Reference: null [WeakReference]
Reference: soft 7 [SoftReference]
Reference: null [WeakReference]
Reference: soft 8 [SoftReference]
Reference: null [WeakReference]
Reference: soft 9 [SoftReference]
Reference: null [WeakReference]
Reference: soft 10 [SoftReference]
Reference: null [WeakReference]
```
**Weak** references and **normal** objects are equally likely to be garbage collected but **soft** references live longer than them.


### Soft reference only
We will create 1000 **soft** references in the loop.

> VM Options: -Xmx3m -Xms3m

```java
public class SoftReferenceExample {
    public static void main(String[] args) {
        List<Reference<MyObject>> references = new ArrayList<>();
        IntStream.range(1, 1001).forEach(i -> {
            MyObject myObject = new MyObject("soft " + i);
            Reference<MyObject> ref = new SoftReference<>(myObject);
            references.add(ref);
        });
        ReferencePrintUtil.printReferences(references);
    }
}
```
This will output:
```log
-- printing references --
Reference: soft 1 [SoftReference]
Reference: soft 2 [SoftReference]
Reference: soft 3 [SoftReference]
Reference: soft 4 [SoftReference]
Reference: soft 5 [SoftReference]
Reference: soft 6 [SoftReference]
Reference: soft 7 [SoftReference]

...

Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
Reference: null [SoftReference]
```
This time some of the **soft** references were garbage collected.


## Phantom Reference Example

### Phantom Example 1

> VM Options: -Xmx1m -Xms1ms

```java
package com.nobodyhub.learn.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceExample1 {
    public static void main(String[] args) {
        ReferenceQueue<MyObject> queue = new ReferenceQueue<>();

        MyObject obj1 = new MyObject("phantom");
        Reference<MyObject> ref = new PhantomReference<>(obj1, queue);
        System.out.println("ref#get()" + ref.get());

        MyObject obj2 = new MyObject("normal");

        obj1 = null;
        obj2 = null;

        System.out.println("-- 1st Check --");
        if(checkObjectGced(ref, queue)){
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
            System.out.println("Ref#get(): " + polledRef.get());
        }
        return gc;
    }

    private static void takeAction() {
        System.out.println("pre-mortem cleanup actions");
    }
}
```
This will output:
```log
ref#get(): null
-- 1st Check --
-- Checking whether object garbage collection due --
polledRef: null
Is polledRef same: false
-- do some memory intensive work --
Finalizing: normal
Finalizing: phantom
-- 2nd Check --
-- Checking whether object garbage collection due --
polledRef: java.lang.ref.PhantomReference@4554617c
Is polledRef same: true
ref#get(): null
pre-mortem cleanup actions
```
||ref#get()|polledRef|polledRef == ref|
|:-:|:-:|:-:|:-:|
|Initial|null|-|-|
|1st Check|null|null|false|
|2nd Check|null|not-null|true|

* **ref#get()** will always return *null*
* **ReferenceQueue#poll()** returns the same ref instance just after `finalize()` method is called. In fact only those References are enqueued which have been finalized already.
* seems no difference between a **norma** object and **phantom** referenced object with regards to when they are GCed

> **[Notes for Phantom Reference](https://www.oracle.com/technetwork/java/javase/9-notes-3745703.html#JDK-8071507)**
> * Phantom references are automatically cleared as soft and weak references
>     * This enhancement changes phantom references to be automatically cleared by the garbage collector as soft and weak references.  
>     * An object becomes phantom reachable after it has been finalized. This change may cause the phantom reachable objects to be GC'ed earlier. Previously, the referent was kept alive until PhantomReference objects were GC'ed. This potential behavioral change might only impact existing code that would depend on PhantomReference being enqueued rather than when the referent was freed from the heap.

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
* ReferenceQueue<T>: Reference queues, to which registered reference objects are appended by the garbage collector after the appropriate reachability changes are detected.

## Soft reference vs Normal reference
We will create *soft* reference and *normal* references in the loop.

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
In the loop we created multiple *soft* references which is wrapped with `SoftReference` and let the underlying references go unreachable when the current loop ends.
We also created the same object without wrapping in the `SoftReferences`. Those we named it *normal* in this example. 

We kept the *soft* references in a collection so that we can afterward check how many of them will return null on get() call. 
Keeping an instance of Reference in a collection does not create a strong reference of the underlying object. 
If we kept the *normal* objects straight in a collection, then for sure we would be creating strong references. 

## Weak reference vs Normal reference
We will create *weak* reference and *normal* references in the loop.

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
This time all(or most in some run) of the *weak* references are garbaged-collected as soon as they become unreachable, similar to *normal* objects.

## Soft references vs Weak references
We will create both *soft* and *weak* reference in the loop.

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
All(could be some) *weak* references were garbage collected but soft references were not garbed collected till the end of the execution.


## Soft reference only
We will create 1000 *soft* references in the loop.

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
This time some of the *soft* references were garbage collected.

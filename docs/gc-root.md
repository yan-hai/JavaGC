
## GC Root
Garbage-collection roots are always reachable and will no be garbage-collected. 
There are 4 kinds of GC roots in Java:
* **Local variables**: are kept alive by the stack of a thread. 
* **Active Java threads**: are always considered as live objects. Especially important for thread local variables
* **Static variables**: are referenced by their classes. This fact makes them de facto GC roots.
* **JNI References**: are Java ojbects that the native code has created as part of a JNI call.

> VM Options for example in the section: `-Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails`

### [Local Variables Example 1](../src/main/java/com/nobodyhub/learn/gc/LocalVarExample1.java)
This example shows how the GC treats the not-null local variable.
```java
public class LocalVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        LocalVarExample1 e = new LocalVarExample1();
        System.gc();
        System.out.println("GC Completed!");
    }
}
```
The output will be:
```log
[GC (System.gc()) [PSYoungGen: 66928K->51760K(458752K)] 66928K->51768K(983040K), 0.0335928 secs] [Times: user=0.01 sys=0.02, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51760K->0K(458752K)] [ParOldGen: 8K->51609K(524288K)] 51768K->51609K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0256327 secs] [Times: user=0.02 sys=0.03, real=0.02 secs] 
GC Completed!
```

* In the `Full GC`, `e` was remvoed from `PSYoungGen`, resulting in decrease of `PSYoungGen`.
* Instead of being garbage-collected, the object was moved to `ParOldGen` since there is almost no change in the overall memory(10496K->10398K).

### [Local Variables Example 2](../src/main/java/com/nobodyhub/learn/gc/LocalVarExample2.java)
This example shows how the GC treats the null local variable.

```java
public class LocalVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        LocalVarExample2 e = new LocalVarExample2();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 66928K->512K(458752K)] 66928K->520K(983040K), 0.0009957 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 512K->0K(458752K)] [ParOldGen: 8K->409K(524288K)] 520K->409K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0037865 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
GC Completed!
```

Instead of moving to `ParOldGen`, `e` was removed directly.

### [Local Variables Example 3](../src/main/java/com/nobodyhub/learn/gc/LocalVarExample3.java)
This example shows how GC treats the local variable in the method call.

```java
public class LocalVarExample3 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        method();
        System.gc();
        System.out.println("2nd GC Completed!");
    }

    public static void method() {
        LocalVarExample3 e = new LocalVarExample3();
        System.gc();
        System.out.println("1st GC Completed!");
    }
}
```
The output will be:
```log
[GC (System.gc()) [PSYoungGen: 66928K->51680K(458752K)] 66928K->51688K(983040K), 0.0401186 secs] [Times: user=0.00 sys=0.03, real=0.04 secs] 
[Full GC (System.gc()) [PSYoungGen: 51680K->0K(458752K)] [ParOldGen: 8K->51609K(524288K)] 51688K->51609K(983040K), [Metaspace: 3336K->3336K(1056768K)], 0.0379273 secs] [Times: user=0.02 sys=0.00, real=0.04 secs] 
1st GC Completed!
[GC (System.gc()) [PSYoungGen: 7864K->32K(458752K)] 59473K->51641K(983040K), 0.0008641 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 32K->0K(458752K)] [ParOldGen: 51609K->405K(524288K)] 51641K->405K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0043024 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2nd GC Completed!
```

* In the 1st GC, `e` was moved from `PSYoungGen` to `ParOldGen`.
* In the 2nd GC, `e` was not held by any object and removed from `ParOldGen` because no method referred it any more.

### [Large Object Example](../src/main/java/com/nobodyhub/learn/gc/LargeObjectExample.java)
The example show how GC deal with big object.
```java
public class LargeObjectExample {
    private int _500MB = 500 * 1024 * 1024;
    private byte[] memory = new byte[_500MB];

    public static void main(String[] args) {
        LargeObjectExample e = new LargeObjectExample();
        System.gc();
        System.out.println("GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 15728K->512K(458752K)] 527728K->512520K(983040K), 0.0027282 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 512K->0K(458752K)] [ParOldGen: 512008K->512409K(524288K)] 512520K->512409K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0138663 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
GC Completed!
```
The large object will be directly created in `ParOldGen`.

### [Class Variables Example 1](../src/main/java/com/nobodyhub/learn/gc/ClassVarExample1.java)
This example shows how the GC treat static variable of class.
```java
public class ClassVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private static ClassVarExample1 instance;

    public static void main(String[] args) {
        ClassVarExample1 e = new ClassVarExample1();
        e.instance = new ClassVarExample1();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 118128K->51712K(458752K)] 118128K->51712K(983040K), 0.0477359 secs] [Times: user=0.00 sys=0.03, real=0.05 secs] 
[Full GC (System.gc()) [PSYoungGen: 51712K->0K(458752K)] [ParOldGen: 0K->51609K(524288K)] 51712K->51609K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0596853 secs] [Times: user=0.03 sys=0.03, real=0.06 secs] 
GC Completed!
```

Because the `e` object was set to null, it was garbage-collected during the minor `GC`.
However, the object held by `StaticVarExample.instance` was not garbage-collected and moved to `ParOldGen`

### [Class Variables Example 2](../src/main/java/com/nobodyhub/learn/gc/ClassVarExample2.java)
This example shows how the GC treat non-static variable of class.
```java
public class ClassVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private ClassVarExample2 instance;

    public static void main(String[] args) {
        ClassVarExample2 e = new ClassVarExample2();
        e.instance = new ClassVarExample2();
        e = null;
        System.gc();
        System.out.println("GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 118128K->528K(458752K)] 118128K->536K(983040K), 0.0008932 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 528K->0K(458752K)] [ParOldGen: 8K->409K(524288K)] 536K->409K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0048655 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
GC Completed!
```
Both `e` and `e.instance` were garbaged-collected in the minor `GC`.

### [Block Variables Example 1](../src/main/java/com/nobodyhub/learn/gc/BlockVarExample1.java)
This example shows how the GC treat variable in the block.
```java
public class BlockVarExample1 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        {
            BlockVarExample1 e = new BlockVarExample1();
            System.gc();
            System.out.println("1st GC Completed!");
        }
        System.gc();
        System.out.println("2nd GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 66928K->51680K(458752K)] 66928K->51688K(983040K), 0.0306643 secs] [Times: user=0.03 sys=0.02, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51680K->0K(458752K)] [ParOldGen: 8K->51609K(524288K)] 51688K->51609K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0157436 secs] [Times: user=0.02 sys=0.03, real=0.02 secs] 
1st GC Completed!
[GC (System.gc()) [PSYoungGen: 7864K->64K(458752K)] 59473K->51673K(983040K), 0.0006379 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 64K->0K(458752K)] [ParOldGen: 51609K->51605K(524288K)] 51673K->51605K(983040K), [Metaspace: 3340K->3340K(1056768K)], 0.0122282 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2nd GC Completed!
```
Even though `e` is not accessable from outside block, it is not garbage-colleceted.

### [Block Variables Example 2](../src/main/java/com/nobodyhub/learn/gc/BlockVarExample2.java)
This example shows how the GC treat variable in the block after set null.
```java
public class BlockVarExample2 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];

    public static void main(String[] args) {
        {
            BlockVarExample2 e = new BlockVarExample2();
            e = null;
            System.gc();
            System.out.println("1st GC Completed!");
        }
        System.gc();
        System.out.println("2nd GC Completed!");
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 66928K->528K(458752K)] 66928K->536K(983040K), 0.0013852 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 528K->0K(458752K)] [ParOldGen: 8K->409K(524288K)] 536K->409K(983040K), [Metaspace: 3338K->3338K(1056768K)], 0.0036675 secs] [Times: user=0.05 sys=0.00, real=0.00 secs] 
1st GC Completed!
[GC (System.gc()) [PSYoungGen: 7864K->64K(458752K)] 8273K->473K(983040K), 0.0002775 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 64K->0K(458752K)] [ParOldGen: 409K->405K(524288K)] 473K->405K(983040K), [Metaspace: 3340K->3340K(1056768K)], 0.0032784 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2nd GC Completed!
```
`e` was garbaged-collected in the minor `GC` in the 1st round.

### [Block Variables Example 3](../src/main/java/com/nobodyhub/learn/gc/BlockVarExample3.java)
This example shows how the GC treats variable in the loop.
```java
public class BlockVarExample3 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private final int idx;

    public BlockVarExample3(int idx) {
        this.idx = idx;
    }

    public static void main(String[] args) {
        int i = 1;
        while (i <= 4) {
            BlockVarExample3 e = new BlockVarExample3(i);
            System.gc();
            System.out.println(i++ + " GC Completed!\n=======");
        }
        System.out.println("Final GC Started!=======");
        System.gc();
        System.out.println("Final GC Finished!=======");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize: " + idx);
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 66928K->51712K(458752K)] 66928K->51720K(983040K), 0.0345976 secs] [Times: user=0.02 sys=0.03, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51712K->0K(458752K)] [ParOldGen: 8K->51609K(524288K)] 51720K->51609K(983040K), [Metaspace: 3339K->3339K(1056768K)], 0.0164505 secs] [Times: user=0.03 sys=0.03, real=0.02 secs] 
1 GC Completed!
=======
[GC (System.gc()) [PSYoungGen: 59064K->51264K(458752K)] 110673K->102873K(983040K), 0.0325766 secs] [Times: user=0.05 sys=0.02, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51264K->0K(458752K)] [ParOldGen: 51609K->102806K(524288K)] 102873K->102806K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0185342 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
2 GC Completed!
=======
Finalize: 1
[GC (System.gc()) [PSYoungGen: 66928K->51232K(458752K)] 169734K->154038K(983040K), 0.0075745 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
[Full GC (System.gc()) [PSYoungGen: 51232K->0K(458752K)] [ParOldGen: 102806K->102805K(524288K)] 154038K->102805K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0131656 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
3 GC Completed!
=======
Finalize: 2
[GC (System.gc()) [PSYoungGen: 66928K->51264K(458752K)] 169734K->154069K(983040K), 0.0101373 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
[Full GC (System.gc()) [PSYoungGen: 51264K->0K(458752K)] [ParOldGen: 102805K->102805K(524288K)] 154069K->102805K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0222437 secs] [Times: user=0.03 sys=0.00, real=0.02 secs] 
4 GC Completed!
=======
Finalize: 3
Final GC Started!=======
[GC (System.gc()) [PSYoungGen: 15728K->32K(458752K)] 118534K->102837K(983040K), 0.0016065 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 32K->0K(458752K)] [ParOldGen: 102805K->51605K(524288K)] 102837K->51605K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0067896 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
Final GC Finished!=======
Finalize: 4
```

||1st GC|2nd GC|3rd GC|4th GC|Final|
|:-:|:----:|:----:|:----:|:----:|:---:|
|Young|51712K->0K|51264K->0K|51232K->0K|51264K->0K|32K->0K|
|Old|8K->51609K|51609K->102806K|102806K->102805K|102805K->102805K|102805K->51605K|
|Total|51720K->51609K|102873K->102806K|154038K->102805K|154069K->102805K|102837K->51605K|
|Finalize|||1|2|3|

### [Block Variables Example 4](../src/main/java/com/nobodyhub/learn/gc/BlockVarExample4.java)
This example shows how the GC treats variable in the loop after set null.
```java
public class BlockVarExample4 {
    private int _50MB = 50 * 1024 * 1024;
    private byte[] memory = new byte[_50MB];
    private final int idx;

    public BlockVarExample4(int idx) {
        this.idx = idx;
    }

    public static void main(String[] args) {
        int i = 1;
        while (i <= 4) {
            BlockVarExample4 e = new BlockVarExample4(i);
            e = null;
            System.gc();
            System.out.println(i++ + " GC Completed!");
        }
        System.out.println("Final GC Started!=======");
        System.gc();
        System.out.println("Final GC Finished!=======");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize: " + idx);
    }
}
```
This will output:
```log
[GC (System.gc()) [PSYoungGen: 66928K->51680K(458752K)] 66928K->51688K(983040K), 0.0321865 secs] [Times: user=0.01 sys=0.02, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51680K->0K(458752K)] [ParOldGen: 8K->51609K(524288K)] 51688K->51609K(983040K), [Metaspace: 3339K->3339K(1056768K)], 0.0204054 secs] [Times: user=0.00 sys=0.03, real=0.02 secs] 
1 GC Completed!
Finalize: 1
[GC (System.gc()) [PSYoungGen: 66928K->51264K(458752K)] 118538K->102873K(983040K), 0.0317158 secs] [Times: user=0.00 sys=0.01, real=0.03 secs] 
[Full GC (System.gc()) [PSYoungGen: 51264K->0K(458752K)] [ParOldGen: 51609K->51606K(524288K)] 102873K->51606K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0096857 secs] [Times: user=0.05 sys=0.00, real=0.01 secs] 
2 GC Completed!
Finalize: 2
[GC (System.gc()) [PSYoungGen: 66928K->51264K(458752K)] 118534K->102870K(983040K), 0.0103325 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
[Full GC (System.gc()) [PSYoungGen: 51264K->0K(458752K)] [ParOldGen: 51606K->51605K(524288K)] 102870K->51605K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0110946 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
3 GC Completed!
Finalize: 3
[GC (System.gc()) [PSYoungGen: 66928K->51264K(458752K)] 118534K->102869K(983040K), 0.0080958 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
[Full GC (System.gc()) [PSYoungGen: 51264K->0K(458752K)] [ParOldGen: 51605K->51605K(524288K)] 102869K->51605K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0135345 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
Finalize: 4
4 GC Completed!
Final GC Started!=======
[GC (System.gc()) [PSYoungGen: 15728K->32K(458752K)] 67334K->51637K(983040K), 0.0007039 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 32K->0K(458752K)] [ParOldGen: 51605K->405K(524288K)] 51637K->405K(983040K), [Metaspace: 3341K->3341K(1056768K)], 0.0070613 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
Final GC Finished!=======
```

||1st GC|2nd GC|3rd GC|4th GC|Final|
|:-:|:----:|:----:|:----:|:----:|:---:|
|Young|51680K->0K|51264K->0K|51264K->0K|51264K->0K|32K->0K|
|Old|8K->51609K|51609K->51606K|51606K->51605K|51605K->51605K|51605K->405K|
|Total|51688K->51609|102873K->51606K|102870K->51605K|102869K->51605K|51637K->405K|
|Finalize||1|2|3|4|

## Eligibility for Garbage Collection
> VM Options for example in the section: `-Xms10m -Xmx10m -Xmn10m -XX:+PrintGCDetails`

We will create object uisng following class:
* [Class A](../src/main/java/com/nobodyhub/learn/gc/eligible/A.java) 
```java
public class A {
}
```
* [Class B](../src/main/java/com/nobodyhub/learn/gc/eligible/B.java) 
```java
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
```

### [Eligible Example](../src/main/java/com/nobodyhub/learn/gc/eligible/GcEligibleExample.java)
```java
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
```
This will output:
```log
--- Loop Start ---
[GC (System.gc()) --[PSYoungGen: 2559K->2559K(8704K)] 2559K->2559K(9216K), 0.0043492 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2559K->1192K(8704K)] [ParOldGen: 0K->0K(512K)] 2559K->1192K(9216K), [Metaspace: 3343K->3343K(1056768K)], 0.0048809 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
[GC (System.gc()) --[PSYoungGen: 2127K->2127K(8704K)] 2127K->2167K(9216K), 0.0053990 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2127K->1929K(8704K)] [ParOldGen: 40K->40K(512K)] 2167K->1969K(9216K), [Metaspace: 3344K->3344K(1056768K)], 0.0090495 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
[GC (System.gc()) --[PSYoungGen: 3017K->3017K(8704K)] 3057K->3145K(9216K), 0.0037696 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 3017K->2624K(8704K)] [ParOldGen: 128K->126K(512K)] 3145K->2750K(9216K), [Metaspace: 3344K->3344K(1056768K)], 0.0056003 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
finalized: 1
finalized: 2

...

[GC (System.gc()) --[PSYoungGen: 2634K->2634K(8704K)] 3141K->3141K(9216K), 0.0026652 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2634K->1743K(8704K)] [ParOldGen: 506K->506K(512K)] 3141K->2250K(9216K), [Metaspace: 3902K->3902K(1056768K)], 0.0058493 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
finalized: 998
[GC (System.gc()) --[PSYoungGen: 2634K->2634K(8704K)] 3141K->3141K(9216K), 0.0009503 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2634K->1743K(8704K)] [ParOldGen: 506K->506K(512K)] 3141K->2250K(9216K), [Metaspace: 3902K->3902K(1056768K)], 0.0078499 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
--- Loop End ---
finalized: 999
```

No `OutOfMemoryError` was thrown.

### [Non Eligible Example](../src/main/java/com/nobodyhub/learn/gc/eligible/GcNonEligibleExample.java)
```java
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
```
This will output:

```log
--- Loop Start ---
[GC (System.gc()) --[PSYoungGen: 2559K->2559K(8704K)] 2559K->2567K(9216K), 0.0022721 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2559K->1159K(8704K)] [ParOldGen: 8K->1K(512K)] 2567K->1160K(9216K), [Metaspace: 3237K->3237K(1056768K)], 0.0030886 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (System.gc()) --[PSYoungGen: 2247K->2247K(8704K)] 2249K->2249K(9216K), 0.0008978 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2247K->1936K(8704K)] [ParOldGen: 1K->1K(512K)] 2249K->1937K(9216K), [Metaspace: 3239K->3239K(1056768K)], 0.0043742 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 

...
    
[GC (System.gc()) --[PSYoungGen: 7192K->7192K(8704K)] 7453K->7461K(9216K), 0.0011419 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 7192K->7150K(8704K)] [ParOldGen: 269K->261K(512K)] 7461K->7412K(9216K), [Metaspace: 3311K->3311K(1056768K)], 0.0056924 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
[GC (Allocation Failure) --[PSYoungGen: 7150K->7150K(8704K)] 7412K->7428K(9216K), 0.0006211 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (Ergonomics) [PSYoungGen: 7150K->7150K(8704K)] [ParOldGen: 277K->261K(512K)] 7428K->7412K(9216K), [Metaspace: 3311K->3311K(1056768K)], 0.0029008 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (Allocation Failure) --[PSYoungGen: 7150K->7150K(8704K)] 7412K->7468K(9216K), 0.0004578 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (Allocation Failure) [PSYoungGen: 7150K->7113K(8704K)] [ParOldGen: 317K->280K(512K)] 7468K->7394K(9216K), [Metaspace: 3311K->3311K(1056768K)], 0.0025574 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at com.nobodyhub.learn.gc.eligible.B.<init>(B.java:5)
	at com.nobodyhub.learn.gc.eligible.GcNonEligibleExample.main(GcNonEligibleExample.java:14)
    
```
`OutOfMemoryError` was thrown and `GC (Allocation Failure)`(GC-- if prior to Java 8) happens.

@see more
* [GC--](https://stackoverflow.com/questions/1174976/what-does-gc-mean-in-a-java-garbage-collection-log)
* [GC (Allocation Failure)](https://stackoverflow.com/questions/28342736/java-gc-allocation-failure)

## Format of GC Log
Take the following output as an example:
```
2019-05-06T12:05:42.735+0800: [Full GC (System) [PSYoungGen: 10512K->0K(458752K)] [ParOldGen: 0K->10396K(524288K)] 10512K->10396K(983040K) [Metaspace: 3044K->3044K(21248K)], 0.0088350 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
```
* **2019-05-06T12:05:42.735+0800**: timestamp at which GC ran(if we add option `-XX:+PrintGCDateStamps`).
* **Full Gc**: Type of GC. It could be either `Full GC` or `GC`.
* **[PSYoungGen: 10512K->0K(458752K)]**: After the GC ran, the `young` generation, space came down from 10512K to 0K. Total allocated young generation space is 458752K.
* **[ParOldGen: 10396K->10396K(524288K)]**: After the GC ran the `old` generation space increased from 0K to 10396K and total allocated old generation space is 524288K.
* **10512K->10396K(983040K)**: After the GC ran, overall memory came down from 10512K to 10396K. Total allocated memory space is 983040K.
* **[Metaspace: 3044K->3044K(21248K)]**: After the GC ran, there was no change in the `perm` generation. In version prior to Java 8, it has been renamed to `PSPermGen`.
* **0.0088350 secs**: Time the GC took to complete
* **[Times: user=0.00 sys=0.00, real=0.01 secs]**: 3 types of time are reported for every single GC event. 
    * **real**: is `wall clock` time (time from start to finish of the call). This is all elapsed time including time slices used by other processes and time the process spends blocked (for example if it is waiting for I/O to complete).
    * **user**: is the amount of CPU time spent in `user-mode code` (outside the kernel) within the process. This is only actual CPU time used in executing the process. Other processes, and the time the process spends blocked, do not count towards this figure.
    * **sys**: is the amount of CPU time spent in the `kernel` within the process. This means executing CPU time spent in system calls within the kernel, as opposed to library code, which is still running in user-space.

> Reference: https://dzone.com/articles/understanding-garbage-collection-log

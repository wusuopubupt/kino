#Java面试题总结

### 一. Java内存模型(原子性，可见性，有序性)
原子性：内存操作read, load, assign, use, store, write是原子性的； 另外锁和synchronized也可以实现原子性

可见性：volatile和synchonized

有序性：happens-before原则
#### 1. volatile关键字的作用是什么？

* 保证被volatile修饰的变量对所有线程的可见性， 但由于Java里的运算不是原子性的，所以被volatile修饰的变量在并发下一样是不安全的
* 作为内存屏障(memory barrier)，禁止指令重排序

 
#### 2. Happens-before原则是什么？
如果操作A发生在操作B之前，那么操作A的影响会被操作B观察到，具体包含以下场景：

* 程序次序原则：同一个线程内的代码按次序执行
* 监视器锁原则：一个unlock操作先于后面对同一个锁的lock操作
* valatile变量规则：对一个volatile变量的写操作先于后面对这个变量的读操作
* 线程启动和终止原则：Thread对象的start()方法先行于次线程的所有其他动作，Thread对象内部所有的动作都先于线程终止动作
* 对象终结原则：一个对象的初始化完成happens-before它的finalize()方法的开始
* 传递性：如果 A happens-before 于 B，且 B happens-before C，则 A happens-before C

 
### 二. Java并发
#### 1. 介绍concurrentHashMap的实现原理 
![](images/concurrentHashMap.jpg)

HashEntry的定义：

```java
static final class HashEntry<K,V> { 
       final K key;                     // 声明 key 为 final 型
       final int hash;                  // 声明 hash 值为 final 型 
       volatile V value;                // 声明 value 为 volatile 型
       final HashEntry<K,V> next;       // 声明 next 为 final 型 
 
       HashEntry(K key, int hash, HashEntry<K,V> next, V value) { 
           this.key = key; 
           this.hash = hash; 
           this.next = next; 
           this.value = value; 
       } 
}
```
ConcurrentHashMap 的高并发性主要来自于三个方面：

* 用分离锁(默认16个segment)实现多个线程间的更深层次的共享访问。
* 用 HashEntery 对象的不变性来降低执行读操作的线程在遍历链表期间对加锁的需求。
* 通过对同一个 Volatile 变量的写 / 读访问，协调不同线程间读 / 写操作的内存可见性。

参考：[https://www.ibm.com/developerworks/cn/java/java-lo-concurrenthashmap/](https://www.ibm.com/developerworks/cn/java/java-lo-concurrenthashmap/)


### 三.垃圾回收机制
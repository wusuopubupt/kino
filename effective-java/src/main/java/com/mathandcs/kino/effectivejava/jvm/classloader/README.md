ClassLoader与委派模型(Parents Delegation)
===

>
下面是关于类加载机制的简单连环炮(https://www.zhihu.com/question/27339390)
- 首先肯定是先问你Java的类加载器都有哪些？　　
- 回答了这些以后，可能会问你每个类加载器都加载哪些类？　　
- 说完以后，可能会问你这些类加载之间的父子关系是怎样的？　　
- 你在回答的时候可能会提到双亲委派模型，那么可以继续问你什么是双亲委派模型？　　
- 你解释完了以后，可能会继续问你，为什么Java的类加载器要使用双亲委派模型？　　
- 你回答完以后，可能会继续问你如何自定义自己的类加载器，自己的类加载器和Java自带的类加载器关系如何处理？



ClassLoader顾名思义是类加载器,负责将Class加载到JVM中,其所使用的加载策略叫做**双亲委派模型**.

JVM平台提供三个ClassLoader:

1. **Bootstrap ClassLoader**,由C++实现的类加载器,其主要负责加载JVM自身工作所需要的类,该Loader由JVM自身控制,别人是无法访问的,其也不再双亲委派模型中承担角色.
2. **ExtClassLoader**,该类加载器主要加载System.getProperty("java.ext.dirs")所对应的目录下class文件.一般为JVM平台扩展工具.
3. **AppClassLoader**,该类加载器主要加载 System.getProperty("java.class.path")所对应的目录下的class文件,其委托父类为ExtClassLoader(后面会解释)

![alt tag](./classloader.jpg)

```java
public abstract class ClassLoader {
    // ClassLoader内部持有器父ClassLoader
    private final ClassLoader parent;
    protected Class<?> loadClass(String name, boolean resolve);
    ......
}
```

接下来重点看loadClass()方法,该方法为加载class二进制文件的核心方法.

```java
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    //当父加载器不存在的时候会尝试使用BootStrapClassLoader作为父类
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }
                //c为null则证明父加载器没有加载到,进而使用子类本身的加载策略`findClass()`方法
                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);
                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
    
```
    
那么问题来了：

* **双亲委派模型是什么?**

上述加载流程是：**使用parent加载器加载类 -> parent不存在使用BootStrapClassLoader加载 -> 加载不到则使用子类的加载策略**这里要注意BootStrapClassLoader是由C++实现的JVM内部的加载工具,其没有对应的Java对象,因此不在这个委派体系中,只是相当于名义上的加载器父类. 那么所谓的双亲我认为是parent委托对象与BootStrapClassLoader最顶端的加载器,两者都是属于被委托的对象,那么这就是所谓的双亲委派模型.

那么双亲是什么? 看ClassLoader的注释就能发现这只是个翻译错误,害得我脑补半天,明明是单亲委派,更通俗来说就是一个委托模式,当parent为null的时候,其parent为名义上的BootStrapClassLoader

* **委派模型如何实现?**

实现如上述代码所示,其类本身有private final ClassLoader parent;这一委托父对象,另外其还有虚拟机实现的BootStrapClassLoader这个名义上的父加载器,在方法上优先执行委托类的策略.

* **为什么使用委派模型?**

回答这个问题要先了解Java中是如何判定两个类是同一个类状况,如下段官方所说,也就是类名(包括包名)相同并且他们的类加载器相同,那么两个对象才是等价的.

```shell
At run time, several reference types with the same binary name may be loaded simultaneously by different class loaders. 
These types may or may not represent the same type declaration. 
Even if two such types do represent the same type declaration, they are considered distinct.
```
对于Object类因为父加载器先加载所以能保证对于所有Object的子类其所对应的Object都是由同一个ClassLoader所加载,也就保证了对象相等. 简单来说**委托类优先模式保证了加载器的优先级问题**,让优先级高的ClassLoader先加载,然后轮到优先级低的.
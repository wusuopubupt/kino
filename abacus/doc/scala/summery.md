### 关于Case Class
本质上case class是个语法糖，对你的类构造参数增加了getter访问，还有toString, hashCode, equals 等方法；

最重要的是帮你实现了一个伴生对象，这个伴生对象里定义了apply 方法和 unapply 方法。apply方法是用于在构造对象时，减少new关键字；而unapply方法则是为模式匹配所服务。这两个方法可以看做两个相反的行为，apply是构造(工厂模式)，unapply是分解(解构模式)。case class在暴露了它的构造方式，所以要注意应用场景：当我们想要把某个类型暴露给客户，但又想要隐藏其数据表征时不适宜。

反编译后的Case Class:

``` java
public class B implements scala.Product,scala.Serializable {
    public B copy();
    public java.lang.String productPrefix();
    public int productArity();
    public java.lang.Object productElement(int);
    public scala.collection.Iterator<java.lang.Object> productIterator();
    // 编译器对case类实现了equals/hashCode/toString等方法
    public boolean canEqual(java.lang.Object);
    public int hashCode();
    public java.lang.String toString();
    public boolean equals(java.lang.Object);
    public B();
}
```
反编译后的Case Class伴生对象:

``` java
//伴生对象也混入了AbstractFunction0 和 Serializable 特质
public final class B$ extends scala.runtime.AbstractFunction0<B> implements scala.Serializable {
    public static final B$ MODULE$;
    public static {};
    public final java.lang.String toString();
    public B apply();
    public boolean unapply(B); // 进行构造器模式匹配时的关键
    public java.lang.Object apply();
}
```

### 关于new
在Scala中有这样几种情况，不需要new关键字就可以得到对象的实例。

``` scala
object

object Sample{
}
val instance = Sample
```

使用object去标注的类，就是一个单实例的。在scala语言层面上是不需要new的。

``` scala
case class

case class Sample(x:Int,y:Int){
}
val s = Sample(1,2)
```

伴生对象

``` scala
object Sample{
def apply() = new Sample
}
class Sample{
}
val s = Sample
```

伴生对象这种方式获取实例的情况十分普遍。几乎所有的Scala类都提供了相应的伴生对象的实现。

```scala
val L1 = List(1, 2, 3)
val i = BigDecimal("123.789")
val d = Document
```

那么要解释伴生对象这个情况，就得提及一个方法 apply(注入) 。

apply在数学和计算机科学中，是一个函数，它将函数应用到参数。 用程序员熟悉的话说，就是Java中的工厂模式。 使用工厂方法 apply转换一组参数，创建一个关联类的一个新实例。(case class 实际上是scala编译器会自动的为它生成伴生对象并实现apply方法)

```scala
object Sample {
  def apply(x: Int) = new Sample(x)
}

class Sample(x: Int) {
}

val x = Sample(2) //equivalent to Sample.apply(4)
```

### 关于_
刚刚接触scala的朋友经常会被"_"符号搞得晕头转向。实际上"_"出现在不同的地方含义是有一定区别的。

* 在package中

```scala
import scala.math._ 
import java.util.{ArrayList => _, _}
```
类似Java中包定义中的*，是一个通配符。

* 在参数类型中

``` scala
def sum(x:Int*): Int ={
x.reduce(_+_)
}
val s = sum(1 to 3:_*)
//output: 6
```
_*作为一个整体，告诉编译器你希望将某个参数当作参数序列处理！例如val s = sum(1 to 3:_*)就是将1 to 3当作参数序列处理。PS:*和Java中的可变长参数含义相同。

* 在泛型中

``` scala
case class A[K[_], T](a: K[T])

def foo(l: List[Option[_]]) = {}
```

代表一个运行期存在的类型。

* 在模式匹配中

``` scala
Some(5) match { case Some(_) => println("Yes") } //output: yes
Some("Hello") match { case Some(_) => println("Yes") } //output: yes
```

代表无论是什么值都可以匹配。

* 在成员变量

``` scala
class Sample{
var s:String = _
}
```

初始化默认值。相当于Java中的 String str = null。

* 在函数中

``` scala
val nums = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

nums map (_ + 2)  //equivalent to 'nums map (x=>x+2)'
nums reduce (_ + _) //equivalent to 'nums reduce ((n,n1)=>n+n1)'
```

作为占位符使用

* 在元组中

``` scala
val T1 = (1,"one","一")
T1._1 //output: 1
T1._2 //output: one
T1._3 //output: 一
```

### 关于Option
Java 开发者一般都知道 NullPointerException， 通常这是由于某个方法返回了 null ，但这并不是开发者所希望发生的，代码也不好去处理这种异常，经常会导致程序崩溃。**Scala 试图摆脱 NullPointerException这个问题，并提供自己的类型用来表示一个值是可选的（有值或无值）， 这就是 Option[A] 特质。**

``` scala
// Option.scala

/** Class `Some[A]` represents existing values of type
 *  `A`.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 16/07/2003
 */
final case class Some[+A](x: A) extends Option[A] {
  def isEmpty = false
  def get = x
}


/** This case object represents non-existent values.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 16/07/2003
 */
case object None extends Option[Nothing] {
  def isEmpty = true
  def get = throw new NoSuchElementException("None.get")
}
```

Option[A] 是一个类型为 A 的可选值的容器： 如果值存在， Option[A] 就是一个 Some[A] ，如果不存在， Option[A] 就是对象 None 。

在类型层面上指出一个值是否存在，使用你的代码的开发者（也包括你自己）就会被编译器强制去处理这种可能性， 而不能依赖值存在的偶然性。

``` scala
def getOption(i:Int):Option[Int]={
if(i>0) Some(1) else None
}
getOption(0) match {
case Some(x) =>print _
case None => 
}
```
Scala在很多的集合类中，都提供了这种特性。

``` scala
val M1 = Map("one" -> 1, "two" -> 1)
M1.get("one") // output: Some(1)
M1.get("three") //output: None
```

### 关于.语法
Scala即支持 "." 语法去调用方法，也支持空格然后注明调用的方法这种形式。

``` scala
object Sample {
  def sayHello(name:String): String = {
    s"Hello $name"
  }
}
Sample.sayHello("Stanley")
Sample sayHello "Stanley"
```

上面代码范例中对于，sayHello的调用是等价的。最后一行代码节省了 "." 和 "()" 符号，这种语法设计在符合了Scala一贯精简的作风外，也更接近于自然语言。像之前集合章节中的代码范例中，我们为集合增加一个元素就用 += 就可以了。看起来就是一个自然的数学公式，其本质上不过是List的类定义里面有一个名字叫 "+=" 的成员方法。

``` scala
val L1 = mutable.MutableList(1,2,3)
L1 += 4
L1.+=(4) //Equivalent to 'L1 += 4'
```

关于扩展性
Scala语言的名称来源于"Scalable"(可扩展的)。之所以这样命名，是因为它老斯基设计成可以随着开发者的需求而扩展。为了更好的解释这种扩展性。我们举一个例子

试想一下，当我们想给一个普通的List[Int] 增加一个求方差的能力。参考以下代码范例，首先声明了一个可以求方差的class，然后提供了一个隐式转换的方法。最后，List的实例L1和L2就拥有了求方差的能力。

``` scala
class EnhanceList(val source: List[Int]) {
def variance(): Double = {
val avg = this.source.reduce(_ + _) / source.size
val vari = this.source.map(x => (x - avg) * (x - avg)).reduce(_ + _) / source.size
    vari
  }
}

implicit def list2EnhanceList(l: List[Int]) = new EnhanceList(l)

val L1 = List(50, 100, 100, 60, 50)
val L2 = List(73, 70, 75, 72, 70)
//Increase variance for L1,L2
L1 variance;
L2 variance
```
我们注意最后的两行代码，是不是像List突然增加了求方差的能力呢？当然不是，而是由于Scala编译器强大的类型系统，把L1和L2利用隐式转换成了EnhanceList类。可是，看起来却那么自然的赋予了List求方差的能力。

### 关于yield
yield关键字是可以用来收集结果的表达式。它可以记住每次迭代的值，并可以根据条件决定是否保留。

``` scala
for {
  i <- (1 until 3)
  j <- (3 until 5)
  if((j%i)==0)
} yield (i, j)
//output: Vector((1,3), (1,4), (2,4))
```

### 关于None ,Nil ,Null ,Nothing
Scala中有几个表达空的类型，容易让初学者混淆。

* None是Option的子类型。为了避免令人讨厌的NullPointException异常，Scala设计了一个Option类型。Option类型的返回值只有两个结果Some(x) 和 None。

``` scala
def doOption(i:Int):Option[Int]={
if(i > 10) Some(i) else None
}

doOption(9) match {
case Some(x) => x
case None => 
}
```

* Null是所有AnyRef的子类，在scala的类型系统中，AnyRef是Any的子类，同时Any子类的还有AnyVal。对应java值类型的所有类型都是AnyVal的子类。所以Null可以赋值给所有的引用类型(AnyRef)，不能赋值给值类型，这个java的语义是相同的。 null是Null的唯一对象。
* Nothing是所有类型的子类，也是Null的子类。Nothing没有对象，但是可以用来定义类型。e.g.，如果一个方法抛出异常，则异常的返回值类型就是Nothing

``` scala
def get(index:Int):Int = {
if(index < 0) throw new Exception()
else index
}
get(-1)
```

如果x < 0，抛出异常，返回值的类型为Nothing，Nothing也是Int的子类，所以，if表达式的返回类型为Int，get方法的返回值类型也为Int。 
* Nil是一个空的List，定义为List[Nothing]，根据List的定义List[+A]，所有Nil是所有List[T]的子类。

``` scala
val L1 = 1 :: 2 :: 3 :: Nil
//output: List(1, 2, 3)
```

上面的L1定义如果没有Nil,Scala类型系统无法猜出L1的类型。那么 成员方法 '::' (把元素添加到List方法)就会出现找不到方法的编译错误。
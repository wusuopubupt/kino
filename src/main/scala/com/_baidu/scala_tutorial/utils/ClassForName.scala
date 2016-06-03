class Foo {
  def hello(name: String): String = "Hello there, %s".format(name)
}

object ClassForName {

  def main(args: Array[String]) {
    val foo  = Class.forName("Foo").newInstance.asInstanceOf[{ def hello(name: String): String }]
    println(foo.hello("Walter")) // prints "Hello there, Walter"
  }
}

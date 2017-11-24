package com.mathandcs.kino.abacus.utils

/**
  * @author ${user.name}
  */
object StringUtil {

  def main(args: Array[String]): Unit = {
    // 末尾是":"
    val s = "1:12345:"
    var a = s.split(":")
    println("size: " + a.length) // 2
    for (t <- a) {
      println(t)
    }

    a = s.split(":", 3)
    println("size: " + a.length) // 3
    for (t <- a) {
      println(t)
    }

    a = s.split(":", -1)
    println("size: " + a.length) // 3
    for (t <- a) {
      println(t)
    }

  }
}

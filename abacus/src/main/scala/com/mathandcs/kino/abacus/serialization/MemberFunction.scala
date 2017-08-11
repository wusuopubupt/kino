package com.mathandcs.kino.abacus.serialization

import com.mathandcs.kino.abacus.utils.SparkUtil
import com.mathandcs.kino.abacus.serialization.MemberFunction._
import org.apache.spark.rdd.RDD

/**
  * Created by dashwang on 8/11/17.
  *
  *
  * Spark Task未序列化(Task not serializable)问题分析及解决之二 -- 在map, filter等方法中引用类成员函数
  * http://blog.csdn.net/sogerno1/article/details/45935159
  */
class MemberFunction(conf: String) extends Serializable {
  val sc = SparkUtil.sparkContext
  val list = List("a.com", "www.b.com", "a.cn", "a.com.cn", "a.org")
  val rdd = sc.parallelize(list)

  def getResult(rdd: RDD[String]): Array[String] = {
    // 使用局部变量避免Task not Serializable Exception
    val rootDomain = conf
    val result = rdd.filter(item => item.contains(rootDomain)).map(item => addWWW(item))
    result.collect()
  }

  // 引用了某类的成员函数，会导致该类及所有成员都需要支持序列化
  // 如果定义在这里,在filter中使用时,会抛Task not Serializable Exception (因为sc不可序列化)
  /*
  def addWWW(item: String): String = {
    if (item.startsWith("www")) {
      item
    } else {
      "www." + item
    }
  }
  */
}

object MemberFunction {

  // 把map, filter中要调用的方法定义scala object中, 消除了对MemberFunction类的依赖,
  // 否则,如果定义在MemberFunction类中, 就要求该类的所有成员变量可序列(但sc成员变量是不可序列化的),所以会导致Task not Serializable
  def addWWW(item: String): String = {
    if (item.startsWith("www")) {
      item
    } else {
      "www." + item
    }
  }

  def main(args: Array[String]): Unit = {
    val mf = new MemberFunction("com")
    val rootDomains = mf.getResult(mf.rdd)
    for (domain <- rootDomains) {
      println("domain: " + domain)
    }
  }
}
package com.mathandcs.kino.abacus.examples.serialization

import com.mathandcs.kino.abacus.scala_runtime.utils.SparkUtils
import org.apache.spark.rdd.RDD

/**
  * Created by dashwang on 8/11/17.
  *
  * Spark Task未序列化(Task not serializable)问题分析及解决:
  * http://blog.csdn.net/sogerno1/article/details/45935159
  *
  */
class MemberVariable(conf: String) extends Serializable {
  private val sc = SparkUtils.sparkContext
  val list = List("a.com", "www.b.com", "a.cn", "a.com.cn", "a.org")
  val rdd = sc.parallelize(list)

  private val rootDomain = conf

  /**
    * 由于本方法内部直接使用了类的成员变量sc, 而sc不可序列化, 导致Task not serializable
    *
    * Exception in thread "main" org.apache.spark.SparkException: Task not serializable
    * Caused by: java.io.NotSerializableException: org.apache.spark.SparkContext
    * Serialization stack:
    * - object not serializable (class: org.apache.spark.SparkContext, value: org.apache.spark.SparkContext@1f1117ba)
    * - field (class: com.mathandcs.kino.abacus.serialization.MemberVariable, name: sc, type: class org.apache.spark.SparkContext)
    */
  def getRootDomains1(): Array[String] = {
    rdd.filter(item => item.contains(rootDomain)).collect()
  }

  // 把类成员变量当做参数传入方法,可正确运行
  def getRootDomains2(rdd: RDD[String], rootDomain: String): Array[String] = {
    val result = rdd.filter(item => item.contains(rootDomain))
    result.collect()
  }

  // 方法内部声明局部变量引用类成员, 可正确运行
  def getRootDoamins3(): Array[String] = {
    val localRdd = rdd
    val localRootDomain = rootDomain
    localRdd.filter(item => {
      item.contains(localRootDomain)
    }).collect()
  }
}

class MemberVariable2(conf: String) extends Serializable {
  // 使用关键字“@transent”标注，表示不序列化当前类中的这两个成员变量
  @transient
  private val sc = SparkUtils.sparkContext
  val list = List("a.com", "www.b.com", "a.cn", "a.com.cn", "a.org")
  val rdd = sc.parallelize(list)

  private val rootDomain = conf

  def getRootDomains1(): Array[String] = {
    rdd.filter(item => item.contains(rootDomain)).collect()
  }

}


object MemberVariable {
  def main(args: Array[String]): Unit = {
    val mb = new MemberVariable("com")

    // 产生Task not Serializable 异常
    //val rootDomains = mb.getRootDomains1()

    // 可正确运行
    // val rootDomains = mb.getRootDomains2(mb.rdd, mb.rootDomain)

    // 可正确运行
    var rootDomains = mb.getRootDoamins3()

    for (domain <- rootDomains) {
      println("domain: " + domain)
    }

    val mb2 = new MemberVariable2("com")
    // 可正确运行
    rootDomains = mb2.getRootDomains1()
    for (domain <- rootDomains) {
      println("domain: " + domain)
    }
  }
}

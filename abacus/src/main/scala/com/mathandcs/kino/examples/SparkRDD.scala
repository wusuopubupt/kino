package com.mathandcs.kino.examples

import com.mathandcs.kino.utils.SparkUtil

/**
  * Created by wangdongxu on 10/11/16.
  */
object SparkRDD {
  def main(args: Array[String]) {

    val sc = SparkUtil.sparkContext
    val rdd = sc.textFile("src/main/resources/tags.txt")

    val lines = rdd.collect()

    val first = rdd.first()
    println("rdd first: " + rdd.first())

    val count = rdd.count()
    println("tag counts: " + count)

  }
}
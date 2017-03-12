package com.mathandcs.kino.abacus.examples

import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.rdd.RDD

/**
  * Created by wangdongxu on 10/11/16.
  */
object SparkRDD {
  def main(args: Array[String]) {

    val sc = SparkUtil.sparkContext
    //val rdd = sc.textFile("src/main/resources/tags.txt")

    val rdd = sc.parallelize(List(("Tom", 40), ("Jack", 23), ("Tom", 24)), 3)
    val rdd_map_partitions = rdd.mapPartitionsWithIndex {
      // 'index' represents the Partition No
      // 'iterator' to iterate through all elements
      //                         in the partition
      (index, iterator) => {
        println("Called in Partition -> " + index)
        val myList = iterator.toList
        // In a normal user case, we will do the
        // the initialization(ex : initializing database)
        // before iterating through each element
        myList.map(x => x + " -> " + index).iterator
      }
    }

    println(rdd.partitioner.size)


  }

}
package com.mathandcs.kino.abacus.examples

import com.mathandcs.kino.abacus.utils.SparkUtils
import org.apache.spark.Accumulator

import scala.collection.mutable

/**
  * Created by dashwang on 8/11/16.
  *
  * Spark accumulator使用示例
  * ref: https://github.com/apache/spark/blob/master/core/src/test/scala/org/apache/spark/AccumulatorSuite.scala
  */
object AccumulatorExamples {
  val sc = SparkUtils.sparkContext

  def main(args: Array[String]): Unit = {

    // basic accumulation
    val acc: Accumulator[Int] = sc.accumulator(0)
    val d = sc.parallelize(1 to 20)
    d.foreach(x => acc += x)
    assert(acc.value == 210)

    // collection accumulators
    val maxI = 1000
    val setAcc = sc.accumulableCollection(mutable.HashSet[Int]())
    val bufferAcc = sc.accumulableCollection(mutable.ArrayBuffer[Int]())
    val mapAcc = sc.accumulableCollection(mutable.HashMap[Int, String]())
    val c = sc.parallelize((1 to maxI) ++ (1 to maxI))
    c.foreach {
      x => {
        setAcc += x;
        bufferAcc += x;
        mapAcc += (x -> x.toString)
      }
    }

    for (i <- 1 to maxI) {
      assert(setAcc.value.contains(i))
      assert(bufferAcc.value.toSet.contains(i))
      assert(mapAcc.value.contains(i))
      assert(mapAcc.value.get(i).get == i.toString)
    }
  }
}
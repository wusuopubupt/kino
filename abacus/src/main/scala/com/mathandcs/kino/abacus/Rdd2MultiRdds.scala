package com.mathandcs.kino.abacus

import org.apache.hadoop.fs.Path
import org.apache.spark.rdd.RDD
import org.joda.time.format.DateTimeFormat

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by dashwang on 3/25/17.
  *
  * FROM RDD:
  *
  * output_root/part-00000
  *                .
  *                .
  *             /part-${partitionNum - 1}
  *
  * TO MULTIPLE RDDs:
  *
  * output_root/sub_path1/part-00000
  *                          .
  *                          .
  *                          /part-${fileNumPerPartition - 1}
  *                .
  *                .
  * output_root/sub_path${partitionNum}/part-00000
  *                                          .
  *                                          .
  *                                     /part-${fileNumPerPartition-1}
  */
class Rdd2MultiRdds {

  def rddToMultiRdds(rdd: RDD[String], partitionNum: Int): Array[RDD[String]] = {
    val rdds: Array[RDD[String]] = new Array[RDD[String]](partitionNum)

    for (i <- 0 to partitionNum - 1) {
      val subPathRdd = rdd.mapPartitionsWithIndex {
        case (index, partition) => {
          if (i == index) {
            partition
          } else {
            Iterator()
          }
        }
      }

      rdds(i) = subPathRdd
    }

    rdds
  }

  def parallelSaveMultiRdds(rdds: Array[RDD[String]], partitionNum: Int, fileNumPerPartition: Int,
                            splitOutputRoot: String, startTime: Long, splitIntervalInSec: Int): Array[String] = {

    val subPathList = new Array[String](partitionNum)

    val futures = new Array[Future[Unit]](partitionNum)

    for (i <- 0 to partitionNum - 1) {
      val offset = startTime + i * splitIntervalInSec
      val subPath = DateTimeFormat.forPattern("YYYYMMdd-HHmmss").withZoneUTC().print(offset * 1000)
      subPathList(i) = subPath

      //rdds(i).repartition(fileNumPerPartition).saveAsTextFile(new Path(splitOutputRoot, subPath).toString)

      val f = Future {
        rdds(i).repartition(fileNumPerPartition).saveAsTextFile(new Path(splitOutputRoot, subPath).toString)
      }
      futures(i) = f
    }

    // blocking util all futures done
    Await.result(Future.sequence(futures.toList), Duration.Inf)

    subPathList
  }
}

object Rdd2MultiRdds {
  def main(args: Array[String]): Unit = {

  }
}

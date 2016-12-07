package com.mathandcs.kino.agile.examples

/**
  * Created by wangdongxu on 12/7/16.
  */

object MultiOutputSplit {

  import org.apache.hadoop.io.NullWritable
  import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat
  import org.apache.spark._

  class RDDMultipleTextOutputFormat extends MultipleTextOutputFormat[Any, Any] {
    override def generateActualKey(key: Any, value: Any): Any =
      NullWritable.get()

    override def generateFileNameForKeyValue(key: Any, value: Any, name: String): String =
      key.asInstanceOf[String]
  }

  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("spark-app-template"))

    sc.textFile("file:///tmp/test_data")
      .map{ line =>
        val tokens = line.split(" ")
        (tokens(0), tokens(1))
      } // Your own implementation
      .partitionBy(new HashPartitioner(5))
      .saveAsHadoopFile("file:///tmp/output/path", classOf[String], classOf[String],
        classOf[RDDMultipleTextOutputFormat])
    sc.stop()
  }
}
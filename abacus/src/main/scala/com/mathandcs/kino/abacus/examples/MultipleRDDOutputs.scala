package com.mathandcs.kino.abacus.examples

/**
  * @author ${user.name}
  */

import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat
import org.apache.spark.HashPartitioner

class RDDMultipleTextOutputFormat extends MultipleTextOutputFormat[Any, Any] {
  override def generateActualKey(key: Any, value: Any): Any =
    NullWritable.get()

  override def generateFileNameForKeyValue(key: Any, value: Any, name: String): String = {
    key.asInstanceOf[String]
  }
}



object MultipleRDDOutputs {

  def main(args: Array[String]) {
    val sc = SparkUtil.sparkContext

    val rdd = sc.parallelize(List(("1/1", 40), ("1/2", 23), ("2/1", 24)), 3)
    rdd.partitionBy(new HashPartitioner(2))
      .saveAsHadoopFile("file:///tmp/aaa", classOf[String], classOf[String], classOf[RDDMultipleTextOutputFormat])

    sc.stop()
  }

}

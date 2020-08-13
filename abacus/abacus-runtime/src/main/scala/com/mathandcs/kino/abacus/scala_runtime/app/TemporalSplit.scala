package com.mathandcs.kino.abacus.scala_runtime.app

import com.mathandcs.kino.abacus.config.AppConfig
import com.mathandcs.kino.abacus.scala_runtime.io.DataReader
import com.mathandcs.kino.abacus.scala_runtime.utils.SparkUtils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat
import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.storage.StorageLevel
import org.joda.time.format.DateTimeFormat
import org.json4s.DefaultFormats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, _}
import scala.util.control.Breaks._


/**
  * Created by dash wang on 2/28/17.
  */
class TemporalSplit extends BaseApp {

  private implicit val formats = DefaultFormats

  private val fileNamePrefix = "part-"

  override def run(config: AppConfig) = {
    val sc = SparkUtils.sparkContext

    val timestampCol = config.extra.get("timestampCol").toString
    val fileNumPerPartition = config.extra.get("fileNumPerPartition").asInstanceOf[Int]
    val splitIntervalInSec = config.extra.get("splitIntervalInSec").asInstanceOf[Int]
    val splitOutputRoot = config.outputTables(0).url

    assert(splitIntervalInSec > 0, "split interval is not greater than 0!, error value setted: " + splitIntervalInSec)
    assert(fileNumPerPartition > 0, "file number per partition is not greater than 0, error value setted: " + fileNumPerPartition)
    assert(splitOutputRoot != null && splitOutputRoot.length > 0, "split output path is null")
    assert(timestampCol != null && timestampCol.length > 0, "timestamp column is null")

    val tableDF = DataReader.loadToDataFrame(config.inputTables(0), null)

    // better way: find first null value and break out
    val emptyTimestampColCount = tableDF.filter(s"$timestampCol is null").count()
    if (emptyTimestampColCount > 0) {
      throw new RuntimeException(s"raw input table has $emptyTimestampColCount null value in column $timestampCol, " +
        s"table url: ${config.inputTables(0).url}")
    }

    val lineIdColName = "abacus_platform_generated_col_line_id"
    val rawTableWithIndex = appendIndexToDF(tableDF, lineIdColName)
    val timestampLineIdPairRDD = rawTableWithIndex
      .filter(s"$timestampCol is not null")
      .select(unix_timestamp(rawTableWithIndex.col(timestampCol)), rawTableWithIndex.col(lineIdColName))
      .map(row => (row.getLong(0), row.getLong(1)))
    // (timestamp, lineId)
    timestampLineIdPairRDD.persist(StorageLevel.MEMORY_AND_DISK)

    // get ts column start time and end time
    val tsCol = timestampLineIdPairRDD.map(row => row._1)
    val tsMin = tsCol.min()
    val tsMax = tsCol.max()
    val startTime = getFormattedStartTime(tsMin, splitIntervalInSec)
    val endTime = getFormattedEndTime(tsMax, splitIntervalInSec)

    log.info(s"Start time: ${startTime}")
    log.info(s"End time: ${endTime}")
    assert(endTime > startTime, "end time is not greater than start time!")
    val partitionNum = Math.ceil((endTime - startTime) / splitIntervalInSec).toShort
    log.info(s"Partition num: ${partitionNum}")

    val instanceDF = DataReader.loadToDataFrame(config.inputTables(1), null)
    val joinedPairRdd = broadcastJoin(instanceDF, timestampLineIdPairRDD, startTime, splitIntervalInSec, fileNumPerPartition)
    val subPathList = splitRDD(joinedPairRdd, partitionNum, splitOutputRoot)
    sc.parallelize(subPathList).saveAsTextFile(config.outputTables(0).url)
  }

  def appendIndexToDF(inputDF: DataFrame, indexColName: String): DataFrame = {
    val rddWithIndex = inputDF.rdd.zipWithIndex()
      .map(indexedRow => Row.fromSeq(indexedRow._2 +: indexedRow._1.toSeq))
    val newStructure = StructType(Seq(StructField(indexColName, LongType, true)).++(inputDF.schema.fields))
    val sqlContext = SparkUtils.sqlContext
    sqlContext.createDataFrame(rddWithIndex, newStructure)
  }

  def getFormattedStartTime(startTime: Long, splitIntervalInSec: Int): Long = {
    assert(splitIntervalInSec > 0)
    startTime / splitIntervalInSec * splitIntervalInSec
  }

  def getFormattedEndTime(endTime: Long, splitIntervalInSec: Int): Long = {
    assert(splitIntervalInSec > 0)
    (endTime / splitIntervalInSec + 1) * splitIntervalInSec
  }


  def broadcastJoin(instanceDF: DataFrame, timestampLineIdPairRDD: RDD[(Long, Long)],
                    startTime: Long, splitIntervalInSec: Int, fileNumPerPartition: Int): RDD[(String, String)] = {

    val joinUsingColumn = "lineId"
    val instanceContextColumn = "content"
    val bucketIdColumn = "bucketId"

    object leftTableSchema {
      val lineId = StructField(joinUsingColumn, LongType)
      val content = StructField(instanceContextColumn, StringType)
      val struct = StructType(Array(lineId, content))
    }

    val sqlContext = SparkUtils.sqlContext

    // (lineId, lineContent)
    val dfLeftLarge = instanceDF

    // (lineId, bucketId)
    assert(splitIntervalInSec > 0)
    val bucketSegments = timestampLineIdPairRDD.map(
      x => ((Math.ceil(x._1 - startTime) * 1.0 / splitIntervalInSec).toShort, x._2)
    ).combineByKey(
      createCombiner = (lineid: Long) => new SegmentPartion(lineid),
      mergeValue = (p: SegmentPartion, lineid: Long) => p.put(new SegmentNode(lineid)),
      mergeCombiners = (p: SegmentPartion, sp: SegmentPartion) => p.merge(sp)
    )

    val bs = bucketSegments.collect()
    timestampLineIdPairRDD.unpersist()
    val broadcastBucketSegments = SparkUtils.sparkContext.broadcast(bs)

    dfLeftLarge.mapPartitions(
      iterator => {
        var bid = -1
        val rand = scala.util.Random
        iterator.map(
          row => {
            val lineId = row.getAs[Long](joinUsingColumn)
            val bucketId = {
              bid = -1
              for (bs <- broadcastBucketSegments.value) {
                breakable(
                  // todo: resolve not found cornercase
                  if (bs._2.contains(lineId)) {
                    bid = bs._1
                    break()
                  }
                )
              }
              bid
            }
            val rank = rand.nextInt(fileNumPerPartition)
            val key = bucketId + "/" + fileNamePrefix + rank
            (key, row.getAs[String](instanceContextColumn))
          }
        )
      }
    )
  }

  def splitRDD(rdd: RDD[(String, String)], partitionNum: Short, splitOutputRoot: String): Array[String] = {
    val subPathList = new Array[String](partitionNum)
    for (i <- 0 to partitionNum - 1) {
      subPathList(i) = i.toString
    }

    // faster than loop filter + saveAsHadoopFile
    rdd.partitionBy(new SuffixIntPartitioner(partitionNum)).saveAsHadoopFile(splitOutputRoot,
      classOf[String], classOf[String], classOf[RDDMultipleTextOutputFormat])

    subPathList
  }

  @deprecated
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

  @deprecated
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

  // FROM RDD:
  //
  //  output_root/part-00000
  //                .
  //                .
  //             /part-${partitionNum - 1}
  //
  //  TO MULTIPLE RDDs:
  //
  //  output_root/sub_path1/part-00000
  //                          .
  //                          .
  //                          /part-${fileNumPerPartition - 1}
  //                .
  //                .
  //  output_root/sub_path${partitionNum}/part-00000
  //                                          .
  //                                          .
  //                                     /part-${fileNumPerPartition-1}
  @Deprecated
  def saveRddToMultiPathByPartition(rdd: RDD[String], partitionNum: Int, fileNumPerPartition: Int, splitOutputRoot: String,
                                    startTime: Long, splitIntervalInSec: Int): Array[String] = {

    val subPathList = new Array[String](partitionNum)

    for (i <- 0 to partitionNum - 1) {
      val rdds: Array[RDD[String]] = new Array[RDD[String]](partitionNum)

      val subPathRdd = rdd.mapPartitionsWithIndex {

        case (index, partition) => {
          if (i == index) {
            partition
          } else {
            Iterator()
          }
        }

      }.repartition(fileNumPerPartition)

      val offset = startTime + i * splitIntervalInSec
      val subPath = DateTimeFormat.forPattern("YYYYMMdd-HHmmss").print(offset * 1000)
      subPathList(i) = subPath

    }

    subPathList
  }
}

class RDDMultipleTextOutputFormat extends MultipleTextOutputFormat[Any, Any] {
  override def generateActualKey(key: Any, value: Any): Any =
    NullWritable.get()

  override def generateFileNameForKeyValue(key: Any, value: Any, name: String): String = {
    key.asInstanceOf[String]
  }
}

class SuffixIntPartitioner(partitions: Int) extends Partitioner {
  override def numPartitions: Int = partitions

  override def getPartition(key: Any): Int = {
    // key is: bucketId/rank
    val tokens = key.asInstanceOf[String].split("/")
    val bucketId = tokens(0)
    val rank = tokens(1)
    bucketId.toShort % partitions
  }
}

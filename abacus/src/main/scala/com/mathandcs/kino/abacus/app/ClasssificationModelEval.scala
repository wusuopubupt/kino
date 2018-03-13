package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.app.ClassificationModelEval._
import com.mathandcs.kino.abacus.config.AppConfig
import com.mathandcs.kino.abacus.io.DataReader
import com.mathandcs.kino.abacus.utils.{HDFSUtils, SparkUtils}
import org.apache.spark.Partitioner
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StringType
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by dash wang on 2/7/17.
  *
  * mapPartitions + 二次排序 统计分组auc
  */
class ClassificationModelEval extends BaseApp {

  implicit val formats = DefaultFormats

  override def run(config: AppConfig): Unit = {
    val rawInputDF = DataReader.loadToDataFrame(config.inputTables(0), null)

    val scoreField = config.extra.get("scoreField").toString
    val labelField = config.extra.get("labelField").toString
    val specifiedField = config.extra.get("specifiedField").toString
    val url = config.extra.get("url").toString

    val groupAuc = getGroupedAuc(rawInputDF, scoreField, labelField, specifiedField, url)
    val metrics = new Metrics(groupAuc)
    val jsonContent = write(metrics)
    log.info(s"Calculation completed, report json content is: $jsonContent")
    val metricsRdd = SparkUtils.sparkContext.makeRDD(Seq(jsonContent))
    metricsRdd.saveAsTextFile(config.outputTables(0).url)
  }

  def getGroupedAuc(rawInputDF: DataFrame, scoreField: String, labelField: String, specifiedField: String,
                    uri: String): GroupedAuc = {

    val scratch = generateGroupedScratch(rawInputDF, scoreField, labelField, specifiedField, uri)

    val totalInstanceNum = scratch.totalInstanceNum
    val totalGroupNum = scratch.totalGroupNum
    val specifiedFieldContent = scratch.specifiedFieldContent
    val instanceNum = scratch.instanceNum
    val groupAuc = scratch.groupAuc
    val instanceRatio = scratch.instanceRatio
    val instanceFlag = scratch.instanceFlag
    var fullNegGroupCount = scratch.fullNegGroupCount
    var fullNegInstanceCount = scratch.fullNegInstanceCount
    var fullPosGroupCount = scratch.fullPosGroupCount
    var fullPosInstanceCount = scratch.fullPosInstanceCount

    val partialGroupedAuc: PartialGroupedAuc = new PartialGroupedAuc(
      specifiedFieldContent.toArray,
      instanceNum.toArray,
      groupAuc.toArray,
      instanceRatio.toArray
    )
    log.info(s"Saving group auc json into $uri")
    val partialGroupedAucRdd = SparkUtils.sparkContext.makeRDD(Seq(write(partialGroupedAuc)))
    HDFSUtils.deleteIfExist(uri)
    SparkUtils.sqlContext.read.json(partialGroupedAucRdd).repartition(1).write.json(uri)

    val fullNegInstanceRatio = if (totalInstanceNum == 0) Double.NaN else fullNegInstanceCount * 1.0 / totalInstanceNum
    val fullPosInstanceRatio = if (totalInstanceNum == 0) Double.NaN else fullPosInstanceCount * 1.0 / totalInstanceNum

    var groupAucWithFullNegFullPosSum: Double = 0
    var groupAucDiscardFullNegFullPosSum: Double = 0
    var weightedSumAucWithFullNegFullPos: Double = 0
    var weightedSumAucDiscardFullNegFullPos: Double = 0
    for (i <- instanceNum.indices) {
      groupAucWithFullNegFullPosSum += (if (groupAuc(i).isNaN) 0.5 else groupAuc(i))
      groupAucDiscardFullNegFullPosSum += (if (groupAuc(i).isNaN) 0 else groupAuc(i))
      weightedSumAucWithFullNegFullPos += instanceNum(i) * (if (groupAuc(i).isNaN) 0.5 else groupAuc(i))
      weightedSumAucDiscardFullNegFullPos += instanceNum(i) * (if (groupAuc(i).isNaN) 0 else groupAuc(i))
    }

    val groupAveAucWithFullNegFullPos = groupAucWithFullNegFullPosSum / totalGroupNum
    val groupAveAucDiscardFullNegFullPos = if (totalGroupNum - fullNegGroupCount - fullPosGroupCount == 0) {
      Double.NaN
    } else {
      groupAucDiscardFullNegFullPosSum / (totalGroupNum - fullNegGroupCount - fullPosGroupCount)
    }

    val weightGroupAveAucWithFullNegFullPos = weightedSumAucWithFullNegFullPos / totalInstanceNum
    val weightGroupAveAucDiscardFullNegFullPos = if (totalInstanceNum - fullNegInstanceCount - fullPosInstanceCount == 0) {
      Double.NaN
    } else {
      weightedSumAucDiscardFullNegFullPos / (totalInstanceNum - fullNegInstanceCount - fullPosInstanceCount)
    }

    val groupedAuc = new GroupedAuc(
      specifiedField,
      specifiedFieldContent.toArray.slice(0, 99),
      instanceNum.toArray.slice(0, 99),
      instanceFlag.toArray.slice(0, 99),
      groupAuc.toArray.slice(0, 99),
      instanceRatio.toArray.slice(0, 99),
      uri,
      totalGroupNum,
      fullNegGroupCount,
      fullNegInstanceCount,
      fullNegInstanceRatio,
      fullPosGroupCount,
      fullPosInstanceCount,
      fullPosInstanceRatio,
      groupAveAucDiscardFullNegFullPos,
      groupAveAucWithFullNegFullPos,
      weightGroupAveAucDiscardFullNegFullPos,
      weightGroupAveAucWithFullNegFullPos
    )

    groupedAuc
  }

  /**
    * traverse label prediction records to calculate group auc
    *
    * @param rawInputDF
    * @param scoreField
    * @param labelField
    * @param specifiedField
    * @param uri
    * @return
    */
  private def generateGroupedScratch(rawInputDF: DataFrame, scoreField: String, labelField: String,
                                     specifiedField: String, uri: String): GroupedScratch = {

    // take necessary column and cast specified to string
    val filteredDF = rawInputDF.select(scoreField, labelField, specifiedField)
      .withColumn("tempCol", rawInputDF.col(specifiedField).cast(StringType))
      .drop(specifiedField)
      .withColumnRenamed("tempCol", specifiedField)

    val totalInstanceNum = filteredDF.count()
    assert(totalInstanceNum > 0)

    // do secondary sort on filtered three column
    val partitions = filteredDF.rdd.getNumPartitions
    val secondarySortedPredictionLabels = filteredDF.rdd.flatMap(row => try {
      Option((new FlightEvalTerm(row.getAs[String](specifiedField), row.getDouble(0), row.getString(1).toDouble), 1))
    } catch {
      case e: NumberFormatException => Option(null)
      case oth: Throwable => throw new RuntimeException(oth)
    }).repartitionAndSortWithinPartitions(new FlightEvalTermPartitioner(partitions))


    val fullNegGroupCount = SparkUtils.sparkContext.accumulator(0L)
    val fullNegGroupInstanceCount = SparkUtils.sparkContext.accumulator(0L)
    val fullPosGroupCount = SparkUtils.sparkContext.accumulator(0L)
    val fullPosGroupInstanceCount = SparkUtils.sparkContext.accumulator(0L)

    // do statistic
    val stat = secondarySortedPredictionLabels.mapPartitions(iter => {
      var preKey: String = null
      var reversePairsNum: Long = 0
      var preReversePairsNum: Long = 0
      var posNum: Long = 0
      var negNum: Long = 0
      val groupInfos = ArrayBuffer.empty[GroupInfo]

      val collectGroupInfo = (posNum: Long, negNum: Long, reversePairs: Long) => {
        // collect
        val instanceNum = posNum + negNum
        val instanceRatio = if (totalInstanceNum == 0) Double.NaN else instanceNum * 1.0 / totalInstanceNum
        val instanceFlag =
          if (posNum == instanceNum)
            InstanceGroupFlatEnum.FullPos.id
          else if (posNum == 0) InstanceGroupFlatEnum.FullNeg.id
          else InstanceGroupFlatEnum.BothPostAndNeg.id
        val auc =
          if (posNum == 0 || negNum == 0) 0.0
          else reversePairs * 1.0 / (posNum * negNum)
        groupInfos += new GroupInfo(
          preKey,
          instanceNum,
          instanceRatio,
          auc,
          instanceFlag
        )
        if (posNum == instanceNum) {
          fullPosGroupCount += 1
          fullPosGroupInstanceCount += instanceNum
        } else if (negNum == instanceNum) {
          fullNegGroupCount += 1
          fullNegGroupInstanceCount += instanceNum
        }
      }
      iter.foreach {
        case (flightEvalTerm, index) => {
          if (null != preKey && !preKey.equals(flightEvalTerm.key)) {
            collectGroupInfo(posNum, negNum, reversePairsNum)
            // begin another one group
            posNum = 0
            negNum = 0
            reversePairsNum = 0
            preReversePairsNum = 0
          }
          if (flightEvalTerm.label >= 0.5) {
            // positive instance
            reversePairsNum += preReversePairsNum
            posNum += 1
          } else {
            // negative instance
            preReversePairsNum += 1
            negNum += 1
          }
          // update key
          preKey = flightEvalTerm.key
          // corner case
          if (null != preKey && iter.isEmpty) {
            // collect
            collectGroupInfo(posNum, negNum, reversePairsNum)
          }
        }
      }
      groupInfos.toIterator
    })

    val groupInfos = stat.collect()
    new GroupedScratch(
      groupInfos.map(_.key),
      groupInfos.map(_.instanceNum),
      groupInfos.map(_.auc),
      groupInfos.map(_.instanceRatio),
      groupInfos.map(_.instanceFlag),
      fullNegGroupCount.value,
      fullNegGroupInstanceCount.value,
      fullPosGroupCount.value,
      fullPosGroupInstanceCount.value,
      totalInstanceNum,
      groupInfos.length
    )
  }

}

object ClassificationModelEval {

  case class GroupedScratch(
                             val specifiedFieldContent: Array[String],
                             val instanceNum: Array[Long],
                             val groupAuc: Array[Double],
                             val instanceRatio: Array[Double],
                             val instanceFlag: Array[Int], // -1: full neg, 1: full pos, 0: both neg and pos
                             val fullNegGroupCount: Long,
                             val fullNegInstanceCount: Long,
                             val fullPosGroupCount: Long,
                             val fullPosInstanceCount: Long,
                             val totalInstanceNum: Long,
                             val totalGroupNum: Long)

  case class FlightEvalTerm(val key: String, val score: Double, val label: Double)

  // secondary sort
  object FlightEvalTerm {
    // order: first key, second score (descending)
    implicit def orderingByKeyScore[A <: FlightEvalTerm]: Ordering[FlightEvalTerm] = {
      Ordering.by(fk => (fk.key, fk.score * -1))
    }
  }

  class FlightEvalTermPartitioner(partitions: Int) extends Partitioner {
    require(partitions >= 0, s"Number of partitions ($partitions) cannot be negative.")

    override def numPartitions: Int = partitions

    override def getPartition(key: Any): Int = {
      val k = key.asInstanceOf[FlightEvalTerm]
      k.key.hashCode % partitions
    }
  }

  case class Metrics(groupedAuc: GroupedAuc)

  case class GroupedAuc(
                         specifiedFieldName: String,
                         specifiedFieldContent: Array[String],
                         instanceNum: Array[Long],
                         instanceFlag: Array[Int],
                         groupAuc: Array[Double],
                         instanceRatio: Array[Double],
                         uri: String,
                         totalGroup: Long,
                         fullNegGroupCount: Long,
                         fullNegInstanceCount: Long,
                         fullNegInstanceRatio: Double,
                         fullPosGroupCount: Long,
                         fullPosInstanceCount: Long,
                         fullPosInstanceRatio: Double,
                         groupAveAucDiscardFullNegFullPos: Double,
                         groupAveAucWithFullNegFullPos: Double,
                         weightGroupAveAucDiscardFullNegFullPos: Double,
                         weightGroupAveAucWithFullNegFullPos: Double
                       )

  case class PartialGroupedAuc(
                                specifiedFieldContent: Array[String],
                                instanceNum: Array[Long],
                                groupAuc: Array[Double],
                                instanceRatio: Array[Double]
                              )

  case class GroupInfo(
                        val key: String,
                        val instanceNum: Long,
                        val instanceRatio: Double,
                        val auc: Double,
                        val instanceFlag: Int)

  object InstanceGroupFlatEnum extends Enumeration {
    type InstanceGroupFlatEnum = Value
    val BothPostAndNeg = Value(0)
    val FullPos = Value(1)
    val FullNeg = Value(-1)
  }
}

package com.mathandcs.kino.abacus.app


import java.sql.{Date, Timestamp}

import com.mathandcs.kino.abacus.app.DataStatistics.StatisticsDataTypeEnum.StatisticsDataTypeEnum
import com.mathandcs.kino.abacus.app.DataStatistics._
import com.mathandcs.kino.abacus.app.config.AppConfig
import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel
import org.json4s.JsonAST.{JNull, JString}
import org.json4s.native.Serialization._
import org.json4s.{CustomSerializer, NoTypeHints, native}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

/**
  * Created by dash wang on 2/28/17.
  */
class DataStatistics extends BaseApp {

  // adaptor of java.sql.Date,
  // refer:https://stackoverflow.com/questions/27086688/json4s-trouble-while-trying-to-convert-json-attribute-to-java-sql-date
  case object SqlDateSerializer extends CustomSerializer[java.sql.Date](format => ( {
    case JString(s) => java.sql.Date.valueOf(s)
    case JNull => null
  }, {
    case d: Date => JString(d.toString())
  }
    )
  )

  implicit val formats = native.Serialization.formats(NoTypeHints) + SqlDateSerializer

  // line number of current table
  var dataCount: Long = 0L

  override def run(config: AppConfig): Unit = {
    val rawInputDF = DataImport.loadTSVToDataFrame(config.inputTables(0).url, config.inputTables(0).schema, null)

    // check partition not exceeding PARTITION_UPPER_BOUND
    if (PARTITION_UPPER_BOUND < rawInputDF.rdd.getNumPartitions) {
      log.info(s"Input partition exceeds $PARTITION_UPPER_BOUND, repartition into $PARTITION_UPPER_BOUND blocks")
      rawInputDF.coalesce(PARTITION_UPPER_BOUND)
    }

    log.info("Begin to assemble report info...")
    val metrics = assembleReport(rawInputDF)
    log.info("Finished assembling report, result is: " + metrics)
    val jsonContent = write(metrics)
    log.info(s"Calculation completed, report json content is: $jsonContent")
    val metricsRdd = SparkUtil.sparkContext.makeRDD(Seq(jsonContent))
    metricsRdd.saveAsTextFile(config.outputTables(0).url)
  }

  def assembleReport(inputDF: DataFrame): List[SingleMetric] = {
    // using linkedHashMap to keep insertion order
    val colDataTypesMap = classifyDataTypes(inputDF)
    val futureBuffer = new ArrayBuffer[Future[SingleMetric]](colDataTypesMap.size)
    val promiseBuffer = new ArrayBuffer[Promise[Unit]](colDataTypesMap.size)
    val promiseFutureBuffer = new ArrayBuffer[Future[Unit]](colDataTypesMap.size)

    log.info("Futures starting")
    val startTime = System.nanoTime()
    for (colDataType <- colDataTypesMap) {
      val dataTypeInfo = colDataType._2
      val columnName = dataTypeInfo.columnName
      val primitiveDataType = dataTypeInfo.columnPrimitiveDataType
      val curColDF = inputDF.select(columnName)
      val f: Future[SingleMetric] = Future {
        dataTypeInfo.statisticsDataType match {
          case StatisticsDataTypeEnum.NumericType => processNumericTypeCol(curColDF, columnName, primitiveDataType)
          case StatisticsDataTypeEnum.DateType => primitiveDataType match {
            case DateType =>
              implicit val clazz = classOf[Date]
              processGenericDateTypeCol[Date](curColDF, columnName)
            case TimestampType =>
              implicit val clazz = classOf[Timestamp]
              processGenericDateTypeCol[Timestamp](curColDF, columnName)
          }
          case StatisticsDataTypeEnum.StringType => processStringTypeCol(curColDF, columnName)
        }
      }
      futureBuffer += f

      val p = Promise[Unit]
      p.future.onSuccess { case _ =>
        println("The program waited patiently for all callbacks to finish.")
      }
      promiseBuffer += p
      promiseFutureBuffer += p.future
    }


    // register callback
    val metrics = new Array[SingleMetric](colDataTypesMap.size)
    for (i <- futureBuffer.indices) {
      futureBuffer(i).onComplete {
        case Success(value) => {
          val estimatedMillSecs = (System.nanoTime() - startTime) / 1e6
          log.info(s"Got future #$i callback $estimatedMillSecs in ms, onComplete value is: $value")
          metrics(i) = value
          log.info("metric is:" + metrics.toList)
          // when futures and corresponding callback are done, promise is succeed
          promiseBuffer(i).success(())
        }
        case Failure(e) => {
          val columnName = colDataTypesMap.get(i)
          log.error(s"Failed to calculate column : $columnName", e)
          promiseBuffer(i).failure(e)
        }
      }
    }

    // block until all futures(BUT NOT corresponding callbacks) are done
    Await.result(Future.sequence(futureBuffer.toList), Duration.Inf)

    // block until all promise done.
    Await.result(Future.sequence(promiseFutureBuffer.toList), Duration.Inf)

    val estimatedMillSecs = (System.nanoTime() - startTime) / 1e6
    log.info(s"Futures ended, estimated $estimatedMillSecs mill seconds")

    metrics.toList
  }

  def getDataCount(df: DataFrame): Long = {
    if (dataCount > 0L) {
      dataCount
    } else {
      dataCount = df.count()
      dataCount
    }
  }

  def processNumericTypeCol(curColDF: DataFrame, columnName: String, dataType: DataType): NumericMetric = {
    dataType match {
      case LongType => processLongTypeCol(curColDF, columnName)
      case IntegerType => processIntTypeCol(curColDF, columnName)
      case DoubleType => processDoubleTypeCol(curColDF, columnName)
    }
  }

  def processDoubleTypeCol(curColDF: DataFrame, columnName: String): NumericMetric = {
    // cache current column DataFrame
    curColDF.persist(StorageLevel.MEMORY_AND_DISK)

    val count = getDataCount(curColDF)
    val zeroCountAcc = SparkUtil.sparkContext.accumulator(0L)
    // filter null value (since null.getAs[Int](0) will return 0)
    val statCounter = curColDF.filter(s"$columnName is not null").map(row => {
      val doubleVal = row.getAs[Double](0)
      // zero count
      if ("0".equals(String.valueOf(doubleVal)) || "0.0".equals(String.valueOf(doubleVal))) {
        zeroCountAcc += 1
      }
      doubleVal
    }).stats()

    val min = statCounter.min
    val max = statCounter.max
    val sum = statCounter.sum
    val mean = sum / count
    val missingRate = (count - statCounter.count) * 1.0 / count
    val zeroRate = (zeroCountAcc.value * 1.0) / count
    val step = (max - min) / BUCKET_NUM

    val brMin = SparkUtil.sparkContext.broadcast(min)
    val brStep = SparkUtil.sparkContext.broadcast(step)
    val brMean = SparkUtil.sparkContext.broadcast(mean)

    val sumOfSquaredDeviationAcc = SparkUtil.sparkContext.accumulator(0.0)

    val arrayOfCountMaps = curColDF.filter(s"$columnName is not null").mapPartitions(iter => {
      val countMap = new mutable.HashMap[Double, Long]()
      iter.foreach(row => {
        val curVal = row.getAs[Double](0)
        // sum of squared deviation
        sumOfSquaredDeviationAcc += math.pow(curVal - brMean.value, 2)
        // graph
        var index = if (brStep.value > 0) (curVal - brMin.value) / brStep.value else 0
        if (index >= BUCKET_NUM) {
          index = BUCKET_NUM - 1
        }
        val count = countMap.getOrElse(index, 0L) + 1L
        countMap.put(index, count)
      })
      Iterator(countMap)
    }).collect()

    curColDF.unpersist()

    // merge maps of each partition
    val mergedCountMap = mutable.Map[Double, Long]()
    for (map <- arrayOfCountMaps) {
      for (kv <- map) {
        val index = kv._1
        val count = mergedCountMap.getOrElse(index, 0L) + kv._2
        mergedCountMap.put(index, count)
      }
    }

    val stdDev = math.sqrt(sumOfSquaredDeviationAcc.value * 1.0 / count)

    val offsetBuffer = ListBuffer[Double]()
    val countBuffer = ListBuffer[Long]()
    for (i <- 0 until BUCKET_NUM) {
      offsetBuffer += "%.2f".format(min + i * step).toDouble
      countBuffer += mergedCountMap.getOrElse(i, 0L)
    }
    val graph = List(offsetBuffer.toList, countBuffer.toList)

    new DoubleMetric(
      StatisticsDataTypeEnum.NumericType.toString,
      columnName,
      classOf[Double].getSimpleName,
      "BussinessNumber",
      "%.2f".format(missingRate).toDouble,
      "%.2f".format(mean).toDouble,
      "%.2f".format(stdDev).toDouble,
      "%.2f".format(zeroRate).toDouble,
      "%.2f".format(min).toDouble,
      "%.2f".format(max).toDouble,
      graph
    )
  }

  def processLongTypeCol(curColDF: DataFrame, columnName: String): NumericMetric = {
    // cache current column DataFrame
    curColDF.persist(StorageLevel.MEMORY_AND_DISK)

    val count = getDataCount(curColDF)
    val zeroCountAcc = SparkUtil.sparkContext.accumulator(0L)
    // filter null value (since null.getAs[Long](0) will return 0)
    val statCounter = curColDF.filter(s"$columnName is not null").map(row => {
      val longVal = row.getAs[Long](0)
      // zero count
      if (0L.equals(longVal)) {
        zeroCountAcc += 1
      }
      longVal
    }).stats()

    val zeroRatio = zeroCountAcc.value * 1.0 / count
    val min = statCounter.min.toLong
    val max = statCounter.max.toLong
    val sum = statCounter.sum
    val mean = sum * 1.0 / count
    val missingRate = (count - statCounter.count) * 1.0 / count
    val step = if ((max - min) >= BUCKET_NUM) (max - min) / BUCKET_NUM else 1

    val brMin = SparkUtil.sparkContext.broadcast(min)
    val brStep = SparkUtil.sparkContext.broadcast(step)
    val brMean = SparkUtil.sparkContext.broadcast(mean)

    val sumOfSquaredDeviationAcc = SparkUtil.sparkContext.accumulator(0.0)

    val arrayOfCountMaps = curColDF.filter(s"$columnName is not null").mapPartitions(iter => {
      val countMap = new mutable.HashMap[Long, Long]()
      iter.foreach(row => {
        val curVal = row.getAs[Long](0)
        // sum of squared deviation
        sumOfSquaredDeviationAcc += math.pow(curVal - brMean.value, 2)
        // graph
        var index = if (brStep.value > 0) (curVal - brMin.value) / brStep.value else 0
        if (index >= BUCKET_NUM) {
          index = BUCKET_NUM - 1
        }
        val count = countMap.getOrElse(index, 0L) + 1L
        countMap.put(index, count)
      })
      Iterator(countMap)
    }).collect()

    curColDF.unpersist()

    // merge maps of each partition
    val mergedCountMap = mutable.Map[Double, Long]()
    for (map <- arrayOfCountMaps) {
      for (kv <- map) {
        val index = kv._1
        val count = mergedCountMap.getOrElse(index, 0L) + kv._2
        mergedCountMap.put(index, count)
      }
    }

    val stdDev = math.sqrt(sumOfSquaredDeviationAcc.value * 1.0 / count)

    val offsetBuffer = ListBuffer[Long]()
    val countBuffer = ListBuffer[Long]()
    for (i <- 0 until BUCKET_NUM) {
      offsetBuffer += min + i * step
      countBuffer += mergedCountMap.getOrElse(i, 0L)
    }
    val graph = List(offsetBuffer.toList, countBuffer.toList)

    new LongMetric(
      StatisticsDataTypeEnum.NumericType.toString,
      columnName,
      classOf[Long].getSimpleName,
      "BussinessNumber",
      "%.2f".format(missingRate).toDouble,
      "%.2f".format(mean).toDouble,
      "%.2f".format(stdDev).toDouble,
      "%.2f".format(zeroRatio).toDouble,
      min,
      max,
      graph
    )
  }

  def processIntTypeCol(curColDF: DataFrame, columnName: String): NumericMetric = {
    // cache current column DataFrame
    curColDF.persist(StorageLevel.MEMORY_AND_DISK)
    val count = getDataCount(curColDF)
    val zeroCountAcc = SparkUtil.sparkContext.accumulator(0L)
    // filter null value (since null.getAs[Int](0) will return 0)
    val statCounter = curColDF.filter(s"$columnName is not null").map(row => {
      val intVal = row.getAs[Int](0)
      // zero count
      if (0 == intVal) {
        zeroCountAcc += 1
      }
      intVal
    }).stats()

    val min = statCounter.min.toInt
    val max = statCounter.max.toInt
    val sum = statCounter.sum
    val mean = sum * 1.0 / count
    val zeroRate = zeroCountAcc.value * 1.0 / count
    val missingRate = (count - statCounter.count) * 1.0 / count
    val step = if ((max - min) >= BUCKET_NUM) (max - min) / BUCKET_NUM else 1

    val brMin = SparkUtil.sparkContext.broadcast(min)
    val brStep = SparkUtil.sparkContext.broadcast(step)
    val brMean = SparkUtil.sparkContext.broadcast(mean)

    val sumOfSquaredDeviationAcc = SparkUtil.sparkContext.accumulator(0.0)

    val arrayOfCountMaps = curColDF.filter(s"$columnName is not null").mapPartitions(iter => {
      val countMap = new mutable.HashMap[Int, Long]()
      iter.foreach(row => {
        val curVal = row.getAs[Int](0)
        // sum of squared deviation
        sumOfSquaredDeviationAcc += math.pow(curVal - brMean.value, 2)
        // graph
        var index = if (brStep.value > 0) (curVal - brMin.value) / brStep.value else 0
        if (index >= BUCKET_NUM) {
          index = BUCKET_NUM - 1
        }
        val count = countMap.getOrElse(index, 0L) + 1L
        countMap.put(index, count)
      })
      Iterator(countMap)
    }).collect()

    curColDF.unpersist()

    // merge maps of each partition
    val mergedCountMap = mutable.Map[Int, Long]()
    for (map <- arrayOfCountMaps) {
      for (kv <- map) {
        val index = kv._1
        val count = mergedCountMap.getOrElse(index, 0L) + kv._2
        mergedCountMap.put(index, count)
      }
    }

    val stdDev = math.sqrt(sumOfSquaredDeviationAcc.value * 1.0 / count)

    val offsetBuffer = ListBuffer[Long]()
    val countBuffer = ListBuffer[Long]()
    for (i <- 0 until BUCKET_NUM) {
      offsetBuffer += min + i * step
      countBuffer += mergedCountMap.getOrElse(i, 0L)
    }
    val graph = List(offsetBuffer.toList, countBuffer.toList)

    new LongMetric(
      StatisticsDataTypeEnum.NumericType.toString,
      columnName,
      classOf[Integer].getSimpleName,
      "BussinessNumber",
      "%.2f".format(missingRate).toDouble,
      "%.2f".format(mean).toDouble,
      "%.2f".format(stdDev).toDouble,
      "%.2f".format(zeroRate).toDouble,
      min,
      max,
      graph
    )
  }

  def processStringTypeCol(curColDF: DataFrame, columnName: String): StringMetric = {
    val missingCountAcc = SparkUtil.sparkContext.accumulator(0L)
    val lineCountAcc = SparkUtil.sparkContext.accumulator(0L)
    val sumLenAcc = SparkUtil.sparkContext.accumulator(0L)

    val wordCount = curColDF.rdd.map(row => {
      val stringVal = row.getAs[String](0)
      if (null != stringVal && !"".equals(stringVal)) {
        sumLenAcc += row.getAs[String](0).length
      } else {
        missingCountAcc += 1L
      }
      lineCountAcc += 1L
      (stringVal, 1L)
    }).reduceByKey(_ + _)

    val uniqueCount = wordCount.count()
    val topItems = if (uniqueCount < MAX_DUPLICATE_COUNT) {
      wordCount.sortBy(x => x._2, ascending = false).take(GRAPH_LIMIT)
    } else {
      Array.empty[(String, Long)]
    }

    val count = lineCountAcc.value
    log.info(s"Line number: $count")
    val missingRate = missingCountAcc.value * 1.0 / count
    val averageLen = sumLenAcc.value * 1.0 / count

    val dateBuffer = ListBuffer[String]()
    val countBuffer = ListBuffer[Long]()
    for (item <- topItems) {
      dateBuffer += item._1
      countBuffer += item._2
    }
    val graph = List(dateBuffer.toList, countBuffer.toList)
    val (top, freqTop) = if (topItems.length > 0) (topItems(0)._1, topItems(0)._2 * 1.0 / count) else ("", 0.0)

    new StringMetric(
      StatisticsDataTypeEnum.StringType.toString,
      columnName,
      classOf[String].getSimpleName,
      "BussinessString",
      "%.2f".format(missingRate).toDouble,
      uniqueCount,
      top,
      "%.2f".format(freqTop).toDouble,
      "%.2f".format(averageLen).toDouble,
      graph
    )
  }

  def initDateClass[T](clazz: java.lang.Class[T])(args: AnyRef*): T = {
    val constructor = clazz.getConstructor(classOf[Long])
    return constructor.newInstance(args: _*).asInstanceOf[T]
  }

  def processGenericDateTypeCol[T >: Null <: java.util.Date : ClassTag](curColDF: DataFrame, columnName: String)
                                                                       (implicit clz: java.lang.Class[T]): GenericDateMetric[T] = {

    val missingCountAcc = SparkUtil.sparkContext.accumulator(0L)
    val lineCountAcc = SparkUtil.sparkContext.accumulator(0L)

    val wordCount = curColDF.rdd.map(row => {
      lineCountAcc += 1L
      val dateVal = row.getAs[T](0)
      val time = if (null == dateVal) {
        missingCountAcc += 1L
        0L
      } else {
        dateVal.getTime
      }
      (time, 1L)
    }).reduceByKey(_ + _)

    val stats = wordCount.map(_._1).filter(_ != 0L).stats()
    val uniqueCount = stats.count

    val args1 = new java.lang.Long(stats.min.toLong)
    val args2 = new java.lang.Long(stats.max.toLong)
    val min: T = initDateClass[T](clz)(args1)
    val max: T = initDateClass[T](clz)(args2)
    val range = (max.getTime - min.getTime) / (1000 * 24 * 60 * 60) + 1

    val topItems = if (uniqueCount < MAX_DUPLICATE_COUNT) {
      wordCount.sortBy(x => x._2, ascending = false).take(GRAPH_LIMIT)
    } else {
      Array.empty[(Long, Long)]
    }

    val count = lineCountAcc.value
    val missingRate = missingCountAcc.value * 1.0 / count

    val dateBuffer = ListBuffer[T]()
    val countBuffer = ListBuffer[Long]()
    for (item <- topItems) {
      val date = if (item._1 == 0L) {
        null
      } else {
        val args = new java.lang.Long(item._1)
        initDateClass[T](clz)(args)
      }
      dateBuffer += date
      countBuffer += item._2
    }
    val graph = List(dateBuffer.toList, countBuffer.toList)

    new GenericDateMetric[T](
      StatisticsDataTypeEnum.DateType.toString,
      columnName,
      clz.getSimpleName,
      "BussinessDateTime",
      missingRate,
      min,
      max,
      range,
      graph
    )
  }

  /**
    * classify columns by data type
    */
  def classifyDataTypes(inputDF: DataFrame): mutable.LinkedHashMap[Int, ColumnDataTypeInfo] = {
    val map = new mutable.LinkedHashMap[Int, ColumnDataTypeInfo]
    val columnNum = inputDF.columns.length
    for (i <- 0 until columnNum) {
      val field = inputDF.schema.fields(i)
      field.dataType match {
        case _: NumericType =>
          map.put(i, new ColumnDataTypeInfo(field.name, field.dataType, StatisticsDataTypeEnum.NumericType))
        case _: DateType | TimestampType =>
          map.put(i, new ColumnDataTypeInfo(field.name, field.dataType, StatisticsDataTypeEnum.DateType))
        case _ =>
          map.put(i, new ColumnDataTypeInfo(field.name, field.dataType, StatisticsDataTypeEnum.StringType))
      }
    }
    map
  }

}

object DataStatistics {

  /** upper bound in case of two many small partitions */
  val PARTITION_UPPER_BOUND = 20

  val MAX_DUPLICATE_COUNT = 1000

  val GRAPH_LIMIT = 5

  val BUCKET_NUM = 10

  trait SingleMetric {
    var valueType: String
  }

  sealed abstract class NumericMetric() extends SingleMetric

  case class DoubleMetric(
                           var valueType: String,
                           name: String,
                           primitiveType: String,
                           businessType: String,
                           missingRate: Double,
                           mean: Double,
                           stdDev: Double,
                           zeros: Double,
                           min: Double,
                           max: Double,
                           graph: List[List[Any]]
                         ) extends NumericMetric

  case class LongMetric(
                         var valueType: String,
                         name: String,
                         primitiveType: String,
                         businessType: String,
                         missingRate: Double,
                         mean: Double,
                         stdDev: Double,
                         zeros: Double,
                         min: Long,
                         max: Long,
                         graph: List[List[Any]]
                       ) extends NumericMetric

  case class StringMetric(
                           var valueType: String = StatisticsDataTypeEnum.StringType.toString,
                           name: String,
                           primitiveType: String,
                           businessType: String,
                           missingRate: Double,
                           unique: Long,
                           top: String,
                           freqTop: Double,
                           averageLen: Double,
                           graph: List[List[Any]]
                         ) extends SingleMetric

  case class GenericDateMetric[T >: Null <: java.util.Date](
                                                             var valueType: String = StatisticsDataTypeEnum.DateType.toString,
                                                             name: String,
                                                             primitiveType: String,
                                                             businessType: String,
                                                             missingRate: Double,
                                                             min: T,
                                                             max: T,
                                                             range: Long,
                                                             graph: List[List[Any]]
                                                           ) extends SingleMetric

  case class ColumnDataTypeInfo(
                                 columnName: String,
                                 columnPrimitiveDataType: DataType,
                                 statisticsDataType: StatisticsDataTypeEnum
                               )

  object StatisticsDataTypeEnum extends Enumeration {
    type StatisticsDataTypeEnum = Value
    val NumericType = Value("numeric")
    val DateType = Value("date")
    val StringType = Value("string")
  }

}

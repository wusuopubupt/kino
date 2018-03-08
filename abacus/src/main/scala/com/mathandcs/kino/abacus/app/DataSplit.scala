package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.app.DataSplit.ColumnNames
import com.mathandcs.kino.abacus.config.AppConfig
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.json4s.DefaultFormats

/**
  * Created by dash wang on 2/28/17.
  *
  * partition by level, then split by ratio
  */
class DataSplit extends BaseApp {

  private implicit val formats = DefaultFormats

  private var configMap = new java.util.HashMap[String, Object]

  override def run(appConfig: AppConfig) = {
    configMap = appConfig.extra
    val splitMethod = DataSplit.MethodEnum(appConfig.extra.get("method").asInstanceOf[Int])
    val df = DataImport.loadToDataFrame(appConfig.inputTables(0), null)
    val splittedDFs = splitMethod match {
      case DataSplit.MethodEnum.SplitByRatio => partitionByLevelAndSplitByRatio(df, configMap.get("levelBy").toString)
      case _ => throw new RuntimeException(s"Unsupported split method: ${splitMethod}")
    }
    for (i <- splittedDFs.indices) {
      splittedDFs(i).rdd.saveAsTextFile(appConfig.outputTables(i).url)
    }
  }

  def partitionByLevelAndSplitByRatio(df: DataFrame, levelBy: String): Array[DataFrame] = {
    // create row id for later join
    val markDF = df.withColumn(ColumnNames.RowId.toString, monotonicallyIncreasingId()).persist(StorageLevel.MEMORY_AND_DISK)

    // make up the tuples to be selected and dropped ie. [row_id] + [level]
    val tuples = markDF.select(markDF(ColumnNames.RowId.toString), markDF(levelBy))
      .repartition(markDF(levelBy)).persist(StorageLevel.MEMORY_AND_DISK)

    // count frequencies which is assumed to be fit into driver memory
    val freq = tuples.mapPartitions(iter => {
      val countMap = new scala.collection.mutable.HashMap[String, Long]()
      iter.foreach(row => {
        // level may be not string
        val level: String =
          if (null == row.get(1))
            null
          else row.get(1).toString
        val newCount: Long = countMap.getOrElse(level, 0L) + 1
        countMap += (level -> newCount)
      })
      Iterator(countMap)
    }).collect()
      .flatten // each single map do not share key
      .toMap

    log.info(s"Frequency is: ${freq}")

    val sc = tuples.sqlContext.sparkContext
    val sqlContext = tuples.sqlContext
    import sqlContext.implicits._
    val brFreq = sc.broadcast(freq)

    // select ids of minor part
    val splitRatio = configMap.get("splitRatio").asInstanceOf[Double]
    val majorFirst = splitRatio > 0.5
    val minorRatio =
      if (majorFirst)
        1.0 - splitRatio
      else splitRatio

    // for later join
    val selectIds = tuples.mapPartitions(iter => {
      val curCountMap = new scala.collection.mutable.HashMap[String, Long]()
      val toCollect = scala.collection.mutable.ArrayBuffer.empty[Long]
      iter.foreach(row => {
        val level: String =
          if (null == row.get(1))
            null
          else row.get(1).toString
        val thresh: Long = (brFreq.value.apply(level) * splitRatio).toLong
        if (thresh > 0) {
          if (!curCountMap.contains(level) || curCountMap.apply(level) < thresh) {
            // collect the header
            if (!majorFirst) toCollect += row.getLong(0)
            val newCount = curCountMap.getOrElse(level, 0L) + 1
            curCountMap += (level -> newCount)
          } else if (majorFirst) {
            // collect the tail
            toCollect += row.getLong(0)
          }
        }
      })
      toCollect.toIterator
    }).toDF(ColumnNames.InclusiveId.toString).cache()

    // for debug
    selectIds.show(100000)

    tuples.unpersist()

    val allRecords = markDF.join(selectIds, markDF(ColumnNames.RowId.toString) ===
      selectIds(ColumnNames.InclusiveId.toString), "left").cache()

    // for debug
    allRecords.show()

    val minorPart = allRecords.filter(allRecords(ColumnNames.InclusiveId.toString).isNotNull)
      .drop(ColumnNames.RowId.toString)
      .drop(ColumnNames.InclusiveId.toString)
      .persist(StorageLevel.MEMORY_AND_DISK)
    val majorPart = allRecords.filter(allRecords("id").isNull)
      .drop(ColumnNames.RowId.toString)
      .drop(ColumnNames.InclusiveId.toString)
      .persist(StorageLevel.MEMORY_AND_DISK)

    markDF.unpersist()
    selectIds.unpersist()
    if (!majorFirst)
      Array(minorPart, majorPart)
    else
      Array(majorPart, minorPart)
  }
}

object DataSplit {

  object MethodEnum extends Enumeration {
    type MethodEnum = Value
    val SplitByRatio = Value(0)
    val SplitByRule = Value(1)
  }

  object ColumnNames extends Enumeration {
    type AppendColumnNames = Value
    val RowId = Value("row_id")
    val InclusiveId = Value("id")
  }

}
package com.mathandcs.kino.abacus.app

import java.sql.{Date, Timestamp}

import com.mathandcs.kino.abacus.app.DataStatistics._
import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.Logging
import org.apache.spark.sql.Row
import org.json4s.JsonAST.{JNull, JString}
import org.json4s.native.Serialization._
import org.json4s.{CustomSerializer, NoTypeHints, native}
import org.scalatest.FlatSpec

/**
  * Created by dash wang on 2/28/17.
  */
class DataStatisticsTest extends FlatSpec with Logging {

  behavior of "data statistics"

  case object DateSerializer extends CustomSerializer[java.sql.Date](format => (
    {
      case JString(s) => Date.valueOf(s)
      case JNull => null
    },
    {
      case d: Date => JString(d.toString())
    }
    )
  )
  implicit val formats =  native.Serialization.formats(NoTypeHints) + DateSerializer

  val precision = 1e-8

  def ~=(x: Double, y: Double, precision: Double) = {
    if ((x - y).abs < precision) true else false
  }

  it should "process Date type column" in {
    alert("processing Date type column")

    val columnName = "date_col"
    import org.apache.spark.sql.types._
    val schema = StructType(List(
      StructField(columnName, DateType, nullable = true)
    ))

    val sc = SparkUtil.sparkContext
    val sqlContext = SparkUtil.sqlContext
    val curColRdd = sc.parallelize(List(
      Row(Date.valueOf("2017-01-01")),
      Row(Date.valueOf("2017-01-01")),
      Row(Date.valueOf("2017-01-01")),
      Row(Date.valueOf("2017-01-02")),
      Row(Date.valueOf("2017-01-02")),
      Row(Date.valueOf("2017-01-02")),
      Row(Date.valueOf("2017-01-03")),
      Row(Date.valueOf("2017-01-03")),
      Row(None),
      Row(Date.valueOf("2017-01-01"))
    ))
    val curColDF = sqlContext.createDataFrame(curColRdd, schema)

    val ds = new DataStatistics
    implicit val clz = classOf[Date]
    val metric:GenericDateMetric[Date] = ds.processGenericDateTypeCol[Date](curColDF, columnName)
    log.info("date col stat metric: " + metric.toString)

    assert(StatisticsDataTypeEnum.DateType.toString.equals(metric.valueType))
    assert(Date.valueOf("2017-01-01").equals(metric.min))
    assert(Date.valueOf("2017-01-03").equals(metric.max))
    assert(3 == metric.range)
    assert(StatisticsDataTypeEnum.DateType.toString.equals(metric.valueType))
    assert(classOf[Date].getSimpleName.equals(metric.primitiveType))
    assert(columnName.equals(metric.name))
    assert(~=(0.1, metric.missingRate, precision))
    assert(4 == (metric.graph(0).size))
    assert(metric.graph == List(List(Date.valueOf("2017-01-01"), Date.valueOf("2017-01-02"), Date.valueOf("2017-01-03"), null), List(4, 3, 2, 1)))
  }

  it should "process TimeStamp type column" in {
    alert("processing TimeStamp type column")

    val columnName = "timestamp_col"
    import org.apache.spark.sql.types._
    val schema = StructType(List(
      StructField(columnName, TimestampType, nullable = true)
    ))

    val sc = SparkUtil.sparkContext
    val sqlContext = SparkUtil.sqlContext
    val curColRdd = sc.parallelize(List(
      Row(Timestamp.valueOf("2017-01-01 01:02:00")),
      Row(Timestamp.valueOf("2017-01-01 01:02:00")),
      Row(Timestamp.valueOf("2017-01-01 01:02:00")),
      Row(Timestamp.valueOf("2017-01-02 01:02:00")),
      Row(Timestamp.valueOf("2017-01-02 01:02:00")),
      Row(Timestamp.valueOf("2017-01-02 01:02:00")),
      Row(Timestamp.valueOf("2017-01-03 01:02:00")),
      Row(Timestamp.valueOf("2017-01-03 01:02:00")),
      Row(None),
      Row(Timestamp.valueOf("2017-01-01 01:02:01"))
    ))
    val curColDF = sqlContext.createDataFrame(curColRdd, schema)

    val ds = new DataStatistics
    implicit val clz = classOf[Timestamp]
    val metric: GenericDateMetric[Timestamp] = ds.processGenericDateTypeCol[Timestamp](curColDF, columnName)
    log.info("timestamp col stat metric: " + metric.toString)
    log.info("timestamp col stat metric json: " + write(metric))

    assert(StatisticsDataTypeEnum.DateType.toString.equals(metric.valueType))
    assert(Timestamp.valueOf("2017-01-01 01:02:00").equals(metric.min))
    assert(Timestamp.valueOf("2017-01-03 01:02:00").equals(metric.max))
    assert(3 == metric.range)
    assert(StatisticsDataTypeEnum.DateType.toString.equals(metric.valueType))
    assert(classOf[Timestamp].getSimpleName.equals(metric.primitiveType))
    assert(columnName.equals(metric.name))
    assert(~=(0.1, metric.missingRate, precision))
    assert(5 == (metric.graph(0).size))
    assert(metric.graph ==List(List(Timestamp.valueOf("2017-01-02 01:02:00.0"), Timestamp.valueOf("2017-01-01 01:02:00.0"),
      Timestamp.valueOf("2017-01-03 01:02:00.0"), null, Timestamp.valueOf("2017-01-01 01:02:01.0")), List(3, 3, 2, 1, 1)))
  }

  it should "process String type column" in {
    alert("processing String type column")

    val columnName = "string_col"
    import org.apache.spark.sql.types._
    val schema = StructType(List(
      StructField(columnName, StringType, nullable = true)
    ))

    val sc = SparkUtil.sparkContext
    val sqlContext = SparkUtil.sqlContext
    val curColRdd = sc.parallelize(List(
      Row(null),
      Row("bb"),
      Row("bb"),
      Row("ccc"),
      Row("ccc"),
      Row("ccc"),
      Row("dddd"),
      Row("dddd"),
      Row("dddd"),
      Row("dddd")
    ))
    val curColDF = sqlContext.createDataFrame(curColRdd, schema)

    curColDF.show()

    val ds = new DataStatistics
    val metric: StringMetric = ds.processStringTypeCol(curColDF, columnName)
    log.info("string col stat metric: " + write(metric))

    assert(StatisticsDataTypeEnum.StringType.toString.equals(metric.valueType))
    assert(classOf[String].getSimpleName.equals(metric.primitiveType))
    assert(columnName.equals(metric.name))
    assert(~=(4, metric.unique, precision))
    assert(~=(2.9, metric.averageLen, precision))
    assert("dddd".equals(metric.top))
    assert(~=(0.4, metric.freqTop, precision))
    assert(4 == (metric.graph(0).size))
    assert(4 == (metric.graph(1).size))
    assert(metric.graph == List(List("dddd", "ccc", "bb", null), List(4, 3, 2, 1)))
    // compare double
    assert(~=(0.1, metric.missingRate, precision))
  }

  it should "process Numeric type column" in {
    alert("processing Numeric type column")

    val intColName = "integer_col"
    val longColName = "long_col"
    val doubleColName = "double_col"
    import org.apache.spark.sql.types._
    val schema = StructType(List(
      StructField(intColName, IntegerType, nullable = true),
      StructField(longColName, LongType, nullable = true),
      StructField(doubleColName, DoubleType, nullable = true)
    ))

    val sc = SparkUtil.sparkContext
    val sqlContext = SparkUtil.sqlContext
    val curColRdd = sc.parallelize(List(
      Row(0, 0L, 1.0),
      Row(0, 2L, 2.0),
      Row(0, 3L, 3.0),
      Row(None, None, 4.0),
      Row(5, 5L, 5.0),
      Row(6, 6L, 6.0),
      Row(7, 7L, 7.0),
      Row(8, 8L, 8.0),
      Row(9, 9L, 9.0),
      Row(10, 10L, 10.0),
      Row(11, 11L, 11.0)
    ))
    val curColDF = sqlContext.createDataFrame(curColRdd, schema)

    curColDF.show()

    curColDF.filter(s"$intColName is not null").show

    val ds = new DataStatistics

    // Int
    val intMetric = ds.processNumericTypeCol(curColDF.select(intColName), intColName, IntegerType).asInstanceOf[LongMetric]
    log.info("int col stat metric: " + write(intMetric))
    assert(StatisticsDataTypeEnum.NumericType.toString.equals(intMetric.valueType))
    assert(classOf[Integer].getSimpleName.equals(intMetric.primitiveType))
    assert(intColName.equals(intMetric.name))
    assert(0 == intMetric.min)
    assert(11 == intMetric.max)
    assert(~=(0.27, intMetric.zeros, precision))
    assert(~=(0.09, intMetric.missingRate, precision))
    assert(~=(5.09, intMetric.mean, precision))
    assert(intMetric.graph == List(List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), List(3, 0, 0, 0, 0, 1, 1, 1, 1, 3)))

    // Long
    val longMetric = ds.processNumericTypeCol(curColDF.select(longColName), longColName, LongType).asInstanceOf[LongMetric]
    log.info("long col stat metric: " + write(longMetric))
    assert(StatisticsDataTypeEnum.NumericType.toString.equals(longMetric.valueType))
    assert(classOf[Long].getSimpleName.equals(longMetric.primitiveType))
    assert(longColName.equals(longMetric.name))
    assert(0L.equals(longMetric.min))
    assert(11L.equals(longMetric.max))
    assert(~=(5.55, longMetric.mean, precision))
    assert(~=(0.09, longMetric.zeros, precision))
    assert(~=(0.09, longMetric.missingRate, precision))
    assert(longMetric.graph == List(List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), List(1, 0, 1, 1, 0, 1, 1, 1, 1, 3)))
  }

  case class Metric(val date: Date, val timestamp: Timestamp)
  it should "test Date serialization" in {
    val metric = new Metric(Date.valueOf("2017-01-01"), Timestamp.valueOf("2017-01-01 01:01:00"))
    log.info(s"metric is " + metric)
    log.info(s"serialized json is: " + write(metric))
    assert("{\"date\":\"2017-01-01\",\"timestamp\":\"2017-01-01T01:01:00.000Z\"}".equals(write(metric)))
  }

  /*
  it should "execute run method" in {
    alert("single-data-statistics-config, will work successfully")
    val args = Array("src/test/resources/DataStat.json")
    val app = new DataStatistics()
    app.execute(args(0))
  }
  */

}
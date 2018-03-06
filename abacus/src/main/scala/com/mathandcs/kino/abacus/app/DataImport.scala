package com.mathandcs.kino.abacus.app

import java.text.SimpleDateFormat

import com.databricks.spark.avro._
import com.mathandcs.kino.abacus.common.{Field, Format, Table}
import com.mathandcs.kino.abacus.utils.{SparkUtil, TypeCast}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructType, _}
import org.apache.spark.sql.{DataFrame, Row}

/**
  * Created by dash wang on 2/28/17.
  */
object DataImport extends Logging {

  // refer: https://spark.apache.org/docs/1.6.1/sql-programming-guide.html
  val scalaTypeToSparkSqlTypeMap: Map[String, DataType] = Map(
    "String" -> StringType,
    "Boolean" -> BooleanType,
    "Date" -> DateType,
    "Timestamp" -> TimestampType,
    "Float" -> FloatType,
    "Double" -> DoubleType,
    "Integer" -> IntegerType,
    "Long" -> LongType,
    "Short" -> ShortType,
    "Byte" -> ByteType
  )

  val COLUMN_SEPARATOR = "\t"

  def loadToDataFrame(t: Table, dateFormat: String): DataFrame = {
    val format = Format.withName(t.format)
    format match {
      case Format.tsv => loadTSVToDataFrame(t.url, t.schema, dateFormat)
      case Format.parquet => loadParquetToDataFrame(t.url)
      case Format.avro => loadAvroToDataFrame(t.url)
      case _ => throw new IllegalArgumentException(s"Unsupported format: $format")
    }
  }

  def loadTSVToDataFrame(inputPath: String, schema: List[Field], dateFormat: String): DataFrame = {
    log.info(s"Loading tsv data from path : $inputPath")

    val dfSchema = transferScalaSchemaToSparkSqlSchema(schema)

    val sc = SparkUtil.sparkContext
    val strRDD = sc.textFile(inputPath)

    val fieldsArray = new Array[Any](dfSchema.length)
    val numDropLines = SparkUtil.sqlContext.sparkContext.accumulator(0)

    val dateFormatter = dateFormat match {
      case null => null
      case _ => new SimpleDateFormat(dateFormat)
    }

    // prerequisite: Column separator has been converted to \t
    val rowRDD: RDD[Row] = strRDD.flatMap(
      line => {
        val tokens = line.split(COLUMN_SEPARATOR, -1)
        if (tokens.length != dfSchema.length) {
          // abandoned if not fail
          numDropLines += 1
          None
        }
        else {
          try {
            tokens.zipWithIndex.foreach { case (str: String, idx: Int) =>
              fieldsArray(idx) = {
                val field = dfSchema.apply(idx)
                TypeCast.castTo(str, field.dataType, field.nullable, true, "", dateFormatter)
              }
            }
            Some(Row.fromSeq(fieldsArray))
          } catch {
            case error@(_: java.lang.NumberFormatException | _: IllegalArgumentException) =>
              throw new RuntimeException(s"Fail to parse line: $line", error)
            case pe: java.text.ParseException =>
              throw new RuntimeException("Fail to parse", pe)
          }
        }
      }
    )

    SparkUtil.sqlContext.createDataFrame(rowRDD, dfSchema)
  }

  def loadParquetToDataFrame(inputPath: String): DataFrame = {
    log.info(s"Loading parquet data from path : $inputPath")
    SparkUtil.sqlContext.read.parquet(inputPath)
  }

  def loadAvroToDataFrame(inputPath: String): DataFrame = {
    log.info(s"Loading avro data from path : $inputPath")
    SparkUtil.sqlContext.read.avro(inputPath)
  }

  def transferScalaSchemaToSparkSqlSchema(scalaSchema: List[Field]) = {
    StructType(scalaSchema.map {
      case (filed) => StructField(filed.colName, transferScalaTypeToSparkSqlType(filed.colType))
    })
  }

  def transferScalaTypeToSparkSqlType(scalaType: String): DataType = {
    try {
      //val scalaType = StringUtils.capitalize(scalaType.toLowerCase())
      if (!scalaTypeToSparkSqlTypeMap.contains(scalaType)) {
        throw new Exception(s"DataType ${scalaType} not found!")
      }
      scalaTypeToSparkSqlTypeMap.apply(scalaType)
    } catch {
      case ni: NoSuchElementException => {
        throw new Exception(s"Invalid datatype: ${scalaType}", ni)
      }
      case t: Throwable => throw new Exception("Invalid schema, error: ", t)
    }
  }

}
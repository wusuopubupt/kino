package com.mathandcs.kino.abacus.scala_runtime.utils

import java.math.BigDecimal
import java.sql.{Date, Timestamp}
import java.text.{SimpleDateFormat, NumberFormat}
import java.util.Locale

import org.apache.spark.sql.types._

import scala.util.Try

/**
  * Utility functions for type casting
  *
  * Copied from [com.databricks.spark.csv.util.TypeCast]
  */
object TypeCast {

  /**
    * Casts given string datum to specified type.
    * Currently we do not support complex types (ArrayType, MapType, StructType).
    *
    * For string types, this is simply the datum. For other types.
    * For other nullable types, this is null if the string datum is empty.
    *
    * @param datum string value
    * @param castType SparkSQL type
    */
  def castTo(
                           datum: String,
                           castType: DataType,
                           nullable: Boolean = true,
                           treatEmptyValuesAsNulls: Boolean = false,
                           nullValue: String = "",
                           dateFormatter: SimpleDateFormat = null): Any = {
    if (datum == nullValue &&
      nullable ||
      (treatEmptyValuesAsNulls && datum == "")){
      null
    } else {
      castType match {
        case _: ByteType => datum.toByte
        case _: ShortType => datum.toShort
        case _: IntegerType => datum.toInt
        case _: LongType => datum.toLong
        case _: FloatType => Try(datum.toFloat)
          .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(datum).floatValue())
        case _: DoubleType => Try(datum.toDouble)
          .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(datum).doubleValue())
        case _: BooleanType => datum.toBoolean
        case _: DecimalType => new BigDecimal(datum.replaceAll(",", ""))
        case _: TimestampType if dateFormatter != null =>
          new Timestamp(dateFormatter.parse(datum).getTime)
        case _: TimestampType => Timestamp.valueOf(datum)
        case _: DateType if dateFormatter != null =>
          new Date(dateFormatter.parse(datum).getTime)
        case _: DateType => Date.valueOf(datum)
        case _: StringType => datum
        case _ => throw new RuntimeException(s"Unsupported type: ${castType.typeName}")
      }
    }
  }

  /**
    * Helper method that converts string representation of a character to actual character.
    * It handles some Java escaped strings and throws exception if given string is longer than one
    * character.
    *
    */
  @throws[IllegalArgumentException]
  def toChar(str: String): Char = {
    if (str.charAt(0) == '\\') {
      str.charAt(1)
      match {
        case 't' => '\t'
        case 'r' => '\r'
        case 'b' => '\b'
        case 'f' => '\f'
        case '\"' => '\"' // In case user changes quote char and uses \" as delimiter in options
        case '\'' => '\''
        case 'u' if str == """\u0000""" => '\u0000'
        case _ =>
          throw new IllegalArgumentException(s"Unsupported special character for delimiter: $str")
      }
    } else if (str.length == 1) {
      str.charAt(0)
    } else {
      throw new IllegalArgumentException(s"Delimiter cannot be more than one character: $str")
    }
  }
}
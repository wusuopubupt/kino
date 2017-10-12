package com.mathandcs.kino.abacus.serialization.json4s

import java.sql.{Date, Timestamp}
import java.util.logging.Logger

import org.apache.spark.Logging
import org.json4s.{CustomSerializer, NoTypeHints}
import org.json4s.JsonAST.{JNull, JString}
import org.json4s.jackson.Serialization
import org.json4s.native.Serialization._

/**
  * Created by dashwang on 10/12/17.
  */
object CustomDateSerializer extends Logging {

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

  implicit val formats = Serialization.formats(NoTypeHints) + SqlDateSerializer

  case class DateMetric(
                         val date: Date,
                         val timestamp: Timestamp
                       )

  def main(args: Array[String]): Unit = {
    val metric = new DateMetric(Date.valueOf("2017-01-01"), Timestamp.valueOf("2017-01-01 01:01:00"))
    log.info(s"metric is " + metric)
    log.info(s"serialized json is: " + write(metric))
    assert("{\"date\":\"2017-01-01\",\"timestamp\":\"2017-01-01T01:01:00.000Z\"}".equals(write(metric)))

  }
}



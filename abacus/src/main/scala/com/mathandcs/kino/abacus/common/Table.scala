package com.mathandcs.kino.abacus.common

/**
  * Created by dash wang on 3/1/17.
  *
  */
class Table {
  var url: String = null
  var schema: List[Field] = null
  var format: String = "tsv"

  /**
    * Auxiliary constructor
    */
  def this(url: String, schema: List[Field], format: String) = {
    this()
    this.url = url
    this.schema = schema
    this.format = format
  }

  def this(url: String, schema: List[Field]) = {
    this()
    this.url = url
    this.schema = schema
  }

}

case class Field(var colName: String, var colType: String)

// TODO: resolve gson deserialize scala enumeration bug
object Format extends Enumeration {
  type Format = Value
  val csv = Value("csv")
  val tsv = Value("tsv")
  val parquet = Value("parquet")
  val avro = Value("avro")
  val orc = Value("orc")
  val json = Value("json")
}

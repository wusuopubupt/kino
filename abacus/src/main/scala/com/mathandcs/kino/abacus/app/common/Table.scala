package com.mathandcs.kino.abacus.app.common

/**
  * Created by dash wang on 3/1/17.
  *
  */
class Table {
  var url: String = null
  var schema: List[Field] = null

  /**
    * Auxiliary constructor
    */
  def this(url: String, schema: List[Field]) = {
    this()
    this.url = url
    this.schema = schema
  }
}

case class Field(var colName: String, var colType: String)

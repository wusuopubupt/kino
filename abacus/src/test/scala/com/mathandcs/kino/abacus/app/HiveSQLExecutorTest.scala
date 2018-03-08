package com.mathandcs.kino.abacus.app

import java.util

import com.mathandcs.kino.abacus.common.Field
import com.mathandcs.kino.abacus.inference.{InferRequest, Table}
import com.mathandcs.kino.abacus.utils.{HDFSUtil, SparkUtil}
import org.apache.spark.Logging
import org.scalatest.FlatSpec

import scala.collection.mutable.ListBuffer

/**
  * Created by dash wang on 3/7/17.
  */
class HiveSQLExecutorTest extends FlatSpec with Logging {

  behavior of "HiveSQLExecutorTest"

  it should "execute run method" in {
    alert("single-data-statistics-config, will work successfully")
    val args = Array("src/test/resources/hive-sql-execution.json")
    val app = new HiveSQLExecutor()
    HDFSUtil.deleteIfExist("file:///Users/dashwang/Project/github/wusuopubupt/kino/abacus/src/test/resources/tmp/sql-execution/data")
    app.execute(args(0))
  }

  it should "infer schema" in {
    val schema = new ListBuffer[Field]
    schema += new Field("col_1", "Date")
    val table = new Table("t1", schema.toList)
    val inputTables = new ListBuffer[Table]
    inputTables += table
    val inferRequest = new InferRequest(inputTables.toList, "select * from t1")
    val inferResponse = HiveSQLExecutor.inferSchema(inferRequest)
    log.info(s"Inferring response is: $inferResponse")
    assert(inferResponse.schema.size == 1)
    assert(inferResponse.schema(0).colName.equals("col_1"))
    assert(inferResponse.schema(0).colType.equals("Date"))
  }

}

package com.mathandcs.kino.abacus.app

import com.mathandcs.kino.abacus.config.AppConfig
import com.mathandcs.kino.abacus.inference.{InferRequest, InferResponse}
import com.mathandcs.kino.abacus.utils.SparkUtil
import org.apache.spark.Logging
import org.apache.spark.sql.{DataFrame, RowFactory}
import org.json4s.DefaultFormats

/**
  * Created by dash wang on 2016-08-16.
  */
class HiveSQLExecutor extends BaseApp {

  private implicit val formats = DefaultFormats

  private var sqlStatement: String = null

  override def run(appConfig: AppConfig): Unit = {

    sqlStatement = appConfig.extra.get("sqlStatement").toString

    val sqlContext = SparkUtil.sqlContext

    for (i <- appConfig.inputTables.indices) {
      try {
        val data = DataImport.loadToDataFrame(appConfig.inputTables(0), null)
        val tableName = appConfig.extra.get("tableNames").asInstanceOf[List[String]](i)
        data.registerTempTable(tableName)
      } catch {
        case ex: Exception => throw new RuntimeException(s"fail to register ${i}th table", ex)
      }
    }

    log.info("registered tables: " + sqlContext.tableNames().mkString(","))

    log.info(sqlStatement)
    var out: DataFrame = null
    val queries = sqlStatement.split(";")
    for (query <- queries) {
      // new line mark should be trimmed
      val trimmedSql: String = query.trim
      if (trimmedSql.length > 0) {
        log.info(s"Executing trimmed sql statement: [$trimmedSql]")
        out = sqlContext.sql(trimmedSql)
      }
      else {
        log.info(s"Skipping empty sql statement, before trimming sql is: [$query]")
      }
    }
  }
}

object HiveSQLExecutor extends Logging {

  def inferSchema(inferRequest: InferRequest): InferResponse = {
    // Create empty dataframe with specified schema
    for (table <- inferRequest.inputTables) {
      val rowRDD = SparkUtil.sparkContext.parallelize(List(RowFactory.create(AnyRef)))
      val structType = DataImport.transferScalaSchemaToSparkSqlSchema(table.schema)
      val df = SparkUtil.switchToHiveContext().sqlContext.createDataFrame(rowRDD, structType)
      df.registerTempTable(table.name)
    }

    var inferResponse: InferResponse = null
    val statements = inferRequest.sqlText.split(";")
    try {
      for (statement <- statements) {
        val trimmedSql = statement.trim
        if (trimmedSql.length > 0) {
          val outputDF = SparkUtil.switchToHiveContext().sqlContext.sql(trimmedSql)
          val outputSchema = DataImport.transferSparkSqlSchemaToScalaSchema(outputDF.schema)
          val errors = List.empty[Error]
          inferResponse = new InferResponse(outputSchema, errors)
        } else {
          log.info("Skipping empty sql statement, before trimming sql is: [{}]", statement)
        }
      }
    } catch {
      case ex: Exception => throw new RuntimeException(s"fail to execute sql ${inferRequest.sqlText}")
    } finally {
      // remove temp tables
      for (table <- inferRequest.inputTables) {
        SparkUtil.switchToHiveContext().sqlContext.dropTempTable(table.name)
      }
    }

    return inferResponse
  }
}
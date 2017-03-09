package com.mathandcs.kino.abacus.app

/**
  * Created by dash wang on 3/8/17.
  */
object AppFactory {

  def produce(appName: String): BaseApp = {
    appName match {
      case "DataStatistics" => new DataStatistics
      case "DataSplit" => new DataSplit
      case "HiveSQLExecutor" => new HiveSQLExecutor
      case "ClassificationModelEval" => new ClassificationModelEval
      case "RegressionModelEval" => new RegressionModelEval
      case "TemporalSplit" => new TemporalSplit
      case _ => throw new IllegalArgumentException(s"Unsupported app name: ${appName}")
    }
  }

}

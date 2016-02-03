package com.mathandcs.kino.abacus.app

import com.google.gson.Gson
import com.mathandcs.kino.abacus.app.config.AppConfig
import org.apache.spark.Logging

/**
  * Created by dash wang on 2/1/17.
  */
trait App {
  def run(config: AppConfig)
}

abstract class BaseApp extends App with Logging with Serializable {

  def execute(configFilePath: String) = {
    val config: AppConfig = new Gson().fromJson(loadJson(configFilePath), classOf[AppConfig])
    run(config)
  }

  def loadJson(path: String) = scala.io.Source.fromFile(path)("UTF-8").getLines().mkString("")

}



package com.mathandcs.kino.abacus.app

import java.lang.reflect.{ParameterizedType, Type}

import org.json4s.jackson.Serialization.read
import com.google.gson._
import com.mathandcs.kino.abacus.config.AppConfig
import com.mathandcs.kino.abacus.io.serialization.GsonListAdapter
import org.apache.spark.Logging
import org.json4s.DefaultFormats

/**
  * Created by dash wang on 2/1/17.
  */
trait App {
  def run(config: AppConfig)
}

abstract class BaseApp extends App with Logging with Serializable {

  def execute(configFilePath: String) = {
    val gson = new GsonBuilder().registerTypeHierarchyAdapter(classOf[List[_]], new GsonListAdapter()).create()
    val config: AppConfig = gson.fromJson(loadJson(configFilePath), classOf[AppConfig])
    run(config)

  }

  def loadJson(path: String) = scala.io.Source.fromFile(path)("UTF-8").getLines().mkString("")

}



package com.mathandcs.kino.abacus.app

import java.lang.reflect.{ParameterizedType, Type}

import org.json4s.jackson.Serialization.read
import com.google.gson._
import com.mathandcs.kino.abacus.config.AppConfig
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

// refer: https://stackoverflow.com/questions/6785465/how-can-i-use-gson-in-scala-to-serialize-a-list
case class GsonListAdapter() extends JsonSerializer[List[_]] with JsonDeserializer[List[_]] {
  import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

  import scala.collection.JavaConverters._

  @throws(classOf[JsonParseException])
  def deserialize(jsonElement: JsonElement, t: Type, jdc: JsonDeserializationContext): List[_] = {
    val p = scalaListTypeToJava(t.asInstanceOf[ParameterizedType]) // Safe casting because List is a ParameterizedType.
    val javaList: java.util.List[_ <: Any] = jdc.deserialize(jsonElement, p)
    javaList.asScala.toList
  }

  override def serialize(obj: List[_], t: Type, jdc: JsonSerializationContext): JsonElement = {
    val p = scalaListTypeToJava(t.asInstanceOf[ParameterizedType]) // Safe casting because List is a ParameterizedType.
    jdc.serialize(obj.asInstanceOf[List[Any]].asJava, p)
  }

  private def scalaListTypeToJava(t: ParameterizedType): ParameterizedType = {
    ParameterizedTypeImpl.make(classOf[java.util.List[_]], t.getActualTypeArguments, null)
  }
}

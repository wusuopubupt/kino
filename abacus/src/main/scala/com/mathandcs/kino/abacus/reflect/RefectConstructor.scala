package com.mathandcs.kino.abacus.reflect

import java.sql.Date

/**
  * Created by dashwang on 10/12/17.
  */
object ReflectConstructor {

  // 反射获取带参数的构造器
  // 参考: https://stackoverflow.com/questions/1641104/instantiate-object-with-reflection-using-constructor-arguments
  def initClass[T](clazz: java.lang.Class[T])(args:AnyRef*): T = {
    //val constructor = clazz.getConstructors()(0)
    val constructor = clazz.getConstructor(classOf[Long])
    return constructor.newInstance(args:_*).asInstanceOf[T]
  }

  def main(args: Array[String]): Unit = {
    val date1 = Date.valueOf("2017-01-01")
    // 需要AnyRef包装类型,把scala.Long转化为java.lang.Long
    val arg = java.lang.Long.valueOf(date1.getTime)
    val date2 = initClass[Date](classOf[Date])(arg)
    assert(date1.getTime == date2.getTime)
  }
}

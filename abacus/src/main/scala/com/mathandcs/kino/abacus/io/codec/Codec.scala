package com.mathandcs.kino.abacus.io.codec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.mathandcs.kino.abacus.utils.ScalaUtils

/**
  * Created by dash wang on 3/13/18.
  *
  * ref:
  * 1. https://www.programcreek.com/scala/java.io.ByteArrayOutputStream
  * 2. [org.apache.spark.streaming.CheckPoint]
  */
object Codec {

  /**
    * Serialize scala object to Byte array
    *
    * @param obj
    * @return
    */
  def serialize(obj: AnyRef): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(bos)
    ScalaUtils.tryWithSafeFinally {
      oos.writeObject(obj)
    } {
      oos.close()
    }
    bos.toByteArray
  }

  /**
    * Deserialize scala object from Byte array
    *
    * @param bytes
    * @tparam T
    * @return
    */
  def deserialize[T >: Null](bytes: Array[Byte]): T = {
    val bis = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bis)
    ScalaUtils.tryWithSafeFinally {
      ois.readObject().asInstanceOf[T]
    } {
      ois.close()
    }
  }

}

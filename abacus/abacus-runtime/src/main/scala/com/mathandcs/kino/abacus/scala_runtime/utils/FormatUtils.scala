package com.mathandcs.kino.abacus.scala_runtime.utils

import java.text.SimpleDateFormat
import java.util.{Date, Locale}


object FormatUtils {

  // Thread safe
  private val dateFormat = new ThreadLocal[SimpleDateFormat]() {
    override def initialValue(): SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
  }

  def formatDate(date: Date): String = dateFormat.get.format(date)

  def formatDate(date: Date, format: String): String = {
    val dateFormat = new ThreadLocal[SimpleDateFormat]() {
      override def initialValue(): SimpleDateFormat = new SimpleDateFormat(format)
    }
    dateFormat.get.format(date)
  }

  def formatDate(timestamp: Long): String = dateFormat.get.format(new Date(timestamp))

  def formatDate(timestamp: Long, format: String): String = {
    val dateFormat = new ThreadLocal[SimpleDateFormat]() {
      override def initialValue(): SimpleDateFormat = new SimpleDateFormat(format)
    }
    dateFormat.get.format(new Date(timestamp))
  }

  def formatBytes(size: Long): String = {
    val TB = 1L << 40
    val GB = 1L << 30
    val MB = 1L << 20
    val KB = 1L << 10

    val (value, unit) = {
      if (size >= 2*TB) {
        (size.asInstanceOf[Double] / TB, "TB")
      } else if (size >= 2*GB) {
        (size.asInstanceOf[Double] / GB, "GB")
      } else if (size >= 2*MB) {
        (size.asInstanceOf[Double] / MB, "MB")
      } else if (size >= 2*KB) {
        (size.asInstanceOf[Double] / KB, "KB")
      } else {
        (size.asInstanceOf[Double], "B")
      }
    }
    "%.1f %s".formatLocal(Locale.US, value, unit)
  }

  def formatDuration(milliseconds: Long): String = {
    if (milliseconds < 100) {
      return "%d ms".format(milliseconds)
    }
    val seconds = milliseconds.toDouble / 1000
    if (seconds < 1) {
      return "%.1f s".format(seconds)
    }
    if (seconds < 60) {
      return "%.0f s".format(seconds)
    }
    val minutes = seconds / 60
    if (minutes < 10) {
      return "%.1f min".format(minutes)
    } else if (minutes < 60) {
      return "%.0f min".format(minutes)
    }
    val hours = minutes / 60
    "%.1f h".format(hours)
  }
}

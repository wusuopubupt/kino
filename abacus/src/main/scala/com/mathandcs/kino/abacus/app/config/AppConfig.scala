package com.mathandcs.kino.abacus.app.config

import com.mathandcs.kino.abacus.app.common.Table

/**
  * Created by dash wang on 2/28/17.
  */
case class AppConfig(var inputTables: List[Table], var outputTables: List[Table], var extra: java.util.Map[String, Object])


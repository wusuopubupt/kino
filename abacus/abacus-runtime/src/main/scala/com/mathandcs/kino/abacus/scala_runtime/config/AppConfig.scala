package com.mathandcs.kino.abacus.config

import com.mathandcs.kino.abacus.common.Table

/**
  * Created by dash wang on 2/28/17.
  */
case class AppConfig(var inputTables: List[Table],
                     var outputTables: List[Table],
                     var extra: java.util.HashMap[String, Object])


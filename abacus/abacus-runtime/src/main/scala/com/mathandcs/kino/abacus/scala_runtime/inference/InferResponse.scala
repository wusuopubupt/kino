package com.mathandcs.kino.abacus.scala_runtime.inference

import com.mathandcs.kino.abacus.common.Field

case class InferResponse(
                          var schema: List[Field],
                          var errors: List[Error]
                        )

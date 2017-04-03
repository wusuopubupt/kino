package com.mathandcs.kino.abacus.ml.util

// scalastyle:off
import org.apache.spark.Logging
import org.scalatest.{FlatSpec, Outcome}

/**
  * Base abstract class for all unit tests in Spark for handling common functionality.
  */
abstract class AbacusFlatSpec extends FlatSpec with Logging {
  // scalastyle:on

  /**
    * Log the suite name and the test name before and after each test.
    *
    * Subclasses should never override this method. If they wish to run
    * custom code before and after each test, they should mix in the
    * {{org.scalatest.BeforeAndAfter}} trait instead.
    */
  final protected override def withFixture(test: NoArgTest): Outcome = {
    val testName = test.text
    val suiteName = this.getClass.getName
    val shortSuiteName = suiteName.replaceAll("org.apache.spark", "o.a.s")
    try {
      logInfo(s"\n\n===== TEST OUTPUT FOR $shortSuiteName: '$testName' =====\n")
      test()
    } finally {
      logInfo(s"\n\n===== FINISHED $shortSuiteName: '$testName' =====\n")
    }
  }

}

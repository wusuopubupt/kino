package com.mathandcs.kino.abacus.ml.evaluation.binary

/**
 * Trait for a binary classification evaluation metric computer.
 */
private[evaluation] trait BinaryClassificationMetricComputer extends Serializable {
  def apply(c: BinaryConfusionMatrix): Double
}

/** Precision. Defined as 1.0 when there are no positive examples. */
private[evaluation] object Precision extends BinaryClassificationMetricComputer {
  override def apply(c: BinaryConfusionMatrix): Double = {
    val totalPositives = c.numTruePositives + c.numFalsePositives
    if (totalPositives == 0) {
      1.0
    } else {
      c.numTruePositives.toDouble / totalPositives
    }
  }
}

/** False positive rate. Defined as 0.0 when there are no negative examples. */
private[evaluation] object FalsePositiveRate extends BinaryClassificationMetricComputer {
  override def apply(c: BinaryConfusionMatrix): Double = {
    if (c.numNegatives == 0) {
      0.0
    } else {
      c.numFalsePositives.toDouble / c.numNegatives
    }
  }
}

/** Recall. Defined as 0.0 when there are no positive examples. */
private[evaluation] object Recall extends BinaryClassificationMetricComputer {
  override def apply(c: BinaryConfusionMatrix): Double = {
    if (c.numPositives == 0) {
      0.0
    } else {
      c.numTruePositives.toDouble / c.numPositives
    }
  }
}

/**
 * F-Measure. Defined as 0 if both precision and recall are 0. EG in the case that all examples
 * are false positives.
 * @param beta the beta constant in F-Measure
 * @see http://en.wikipedia.org/wiki/F1_score
 */
private[evaluation] case class FMeasure(beta: Double) extends BinaryClassificationMetricComputer {
  private val beta2 = beta * beta
  override def apply(c: BinaryConfusionMatrix): Double = {
    val precision = Precision(c)
    val recall = Recall(c)
    if (precision + recall == 0) {
      0.0
    } else {
      (1.0 + beta2) * (precision * recall) / (beta2 * precision + recall)
    }
  }
}

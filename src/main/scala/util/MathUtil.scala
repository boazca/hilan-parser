package util

object MathUtil {

  case class Precision(p: Double)

  implicit class DoubleWithAlmostEquals(val d: Double) extends AnyVal {
    def ~=(d2: Double)(implicit p: Precision = Precision(0.01)): Boolean = (d - d2).abs < p.p
  }

  implicit class Numbers(val str: String) extends AnyVal {
    def removeCommas: String = str.replace(",", "")
  }

}

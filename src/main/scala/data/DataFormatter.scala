package data

import util.MathUtil._

object DataFormatter {

  def formatLine(line: String): Value = {
    val lastOne = takeTheLastOne(line).removeCommas.toDouble
    val pensionData = getPensionData(line.removeCommas)
    Value(line, lastOne, pensionData)
  }

  private def getPensionData(value: String): Option[PensionData] = {
    val pattern = "([0-9.]+) ([0-9.]+) ([0-9.]+) ([0-9.]+)( ([0-9.]+))?".r
    pattern.findFirstMatchIn(value).map(m => {
      if (m.group(6) != null)
        PensionData(m.group(3).toDouble, m.group(6).toDouble)
      else
        PensionData(m.group(3).toDouble, m.group(4).toDouble)
    })
  }

  private def takeTheLastOne(value: String): String = {
    val pattern = "(.*[ ]+|)([0-9.,]+)[ ]?([-]{1}|)?".r
    pattern.findFirstMatchIn(value).map(m => m.group(3) + m.group(2)).getOrElse("Bad Match, Oops...")
  }
}

case class Value(fullLine: String, lastOne: Double, pensionData: Option[PensionData])

case class PensionData(employer: Double, employee: Double)

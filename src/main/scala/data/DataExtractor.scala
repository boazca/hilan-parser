package data

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import util.HebrewUtil
import util.MathUtil._

import scala.util.Try
import scala.util.matching.Regex

object DataExtractor {

  private val pensionPattern = "([0-9.]+) ([0-9.]+) ([0-9.]+)".r

  def parse(payslips: Seq[String]): Map[Key, Map[Date, Value]] =
    payslips.flatMap(extractRelevantData)
      .groupBy(_.key).view.mapValues(_
      .groupBy(_.date).view.mapValues(_
      .head.value).toMap).toMap

  private def extractRelevantData(payslip: String): Seq[UniqueValue] = {
    val date = getDate(payslip)
    val KeyToLines = getLinesByKey(payslip)
    val rawInformation = mergeInfoSplitIntoTwoLines(KeyToLines)
    val uniqueData = for {
      (key, values) <- rawInformation
      (rawLineValues, index) <- values.zipWithIndex
      lineValue = DataFormatter.formatLine(rawLineValues)
      indexedKey = getKeyByIndex(key, index)
    } yield UniqueValue(indexedKey, date, lineValue)
    uniqueData.toSeq
  }

  def getDate(payslip: String): Date = {
    val dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
    val datePattern = "תלוש שכר לחודש: (.*)".r
    val dateInHebrew = datePattern.findAllIn(payslip).matchData.map(m => m.group(1)).mkString
    dateFormat.parse(HebrewUtil.dateConvert(dateInHebrew))
  }

  def getLinesByKey(payslip: String): Map[String, List[String]] = {
    val payslipWithPadding = payslip.replace("\n", "\n\n")
    val infoPattern = "\\n(\\d{3,4} .+?) (([0-9.,]+\\-? ?){1,5})\\s".r
    val info = infoPattern.findAllIn(payslipWithPadding).matchData.map(m => (m.group(1), m.group(2))).toList
    info.groupBy(_._1).map { case (k, list) => (k, list.map(_._2).distinct) }
  }

  def mergeInfoSplitIntoTwoLines(info: Map[String, List[String]]): Map[String, List[String]] =
    info.map {
      case (key, first :: seconds :: Nil) =>
        val matchFirst = pensionPattern.findFirstMatchIn(first.removeCommas)
        val matchSecond = pensionPattern.findFirstMatchIn(seconds.removeCommas)
        val resolved = (matchFirst, matchSecond) match {
          case (Some(f), Some(s)) => mergeEmployerEmployeeFormat(f, s)
          case _ => None
        }
        (key, resolved.map(s => List(s)).getOrElse(first :: seconds :: Nil))
      case t => t
    }

  private def mergeEmployerEmployeeFormat(m1: Regex.Match, m2: Regex.Match): Option[String] = {
    Try {
      val base1 = m1.group(1).toDouble
      val percentage1 = m1.group(2).toDouble
      val result1 = m1.group(3).toDouble
      val base2 = m2.group(1).toDouble
      val percentage2 = m2.group(2).toDouble
      val result2 = m2.group(3).toDouble
      if ((base1 * (percentage1 / 100) ~= result1) && (base2 * (percentage2 / 100) ~= result2) && base1 == base2)
        if (percentage1 > percentage2)
          Some(s"$base1 $percentage1 $result1 $percentage2 $result2")
        else
          Some(s"$base1 $percentage2 $result2 $percentage1 $result1")
      else
        None
    }.toOption.flatten
  }

  private def getKeyByIndex(key: String, index: Int): Key = {
    if (index == 0) Key(key)
    else Key(key, Some("כפילות " + index))
  }

}

case class Key(info: String, notes: Option[String] = None)

case class UniqueValue(key: Key, date: Date, value: Value)

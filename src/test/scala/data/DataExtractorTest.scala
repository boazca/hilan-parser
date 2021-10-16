package data

import java.util.{Date, GregorianCalendar}

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


class DataExtractorTest extends SpecificationWithJUnit {

  "DataExtractor" should {

    "getDate" in new Context {
      DataExtractor.getDate(payslip) must be_===(date)
    }

    "get lines split into key and value" in new Context {
      val info: Map[String, List[String]] = DataExtractor.getLinesByKey(payslip)
      info must haveSize(6)
      info must havePair("1042 ש.נוספות גלובלי" -> List("1,674.67"))
      info must havePair("001 משכורת חודשית" -> List("99,876.54"))
      info must havePair("002 ב.מ קצבה זמני קצבה שכיר-תג04/16." -> List("10,000.00- 3,141.59-", "10,666.00- 3,141.59-"))
      info must havePair("002 ב.מ קצבה זמני פיצויים 04/16" -> List("10,000.00- 1570.795-"))
      info must havePair("680 ישראל PPSE" -> List("1000.00"))
      info must havePair("064 מנורה משלימה קצבה שכיר-תג." -> List("5,001.00 6.25 312.56", "5,001.00 5.75 287.55"))
    }

    "merge info from two lines into one if possible" in new Context {
      val info: Map[String, List[String]] = DataExtractor.getLinesByKey(payslip)
      info must havePair("064 מנורה משלימה קצבה שכיר-תג." -> List("5,001.00 6.25 312.56", "5,001.00 5.75 287.55"))

      val merged: Map[String, List[String]] = DataExtractor.mergeInfoSplitIntoTwoLines(info)
      merged must havePair("064 מנורה משלימה קצבה שכיר-תג." -> List("5001.0 6.25 312.56 5.75 287.55"))
    }

    "parse" in new Context {
      DataExtractor.parse(Seq(payslip)) must containTheSameElementsAs(Seq(
        Key("1042 ש.נוספות גלובלי") -> Map(date -> Value("1,674.67", 1674.67, None)),
        Key("001 משכורת חודשית") -> Map(date -> Value("99,876.54", 99876.54, None)),
        Key("002 ב.מ קצבה זמני קצבה שכיר-תג04/16.") -> Map(date -> Value("10,000.00- 3,141.59-", -3141.59, None)),
        Key("002 ב.מ קצבה זמני קצבה שכיר-תג04/16.", Some("כפילות " + 1)) -> Map(date -> Value("10,666.00- 3,141.59-", -3141.59, None)),
        Key("002 ב.מ קצבה זמני פיצויים 04/16") -> Map(date -> Value("10,000.00- 1570.795-", -1570.795, None)),
        Key("680 ישראל PPSE") -> Map(date -> Value("1000.00", 1000, None)),
        Key("064 מנורה משלימה קצבה שכיר-תג.") -> Map(date -> Value("5001.0 6.25 312.56 5.75 287.55", 287.55, Some(PensionData(312.56, 287.55))))
      ))
    }
  }

  class Context extends Scope {
    val date: Date = new GregorianCalendar(2016, 4, 1).getTime

    val payslip: String =
      """
        |סך-כל התשלומים 10,000.00
        |תלוש שכר לחודש: מאי 2016
        |ניכויי חובה-מסים 3,141.59
        |גילום ב.ל. 111
        |001 משכורת חודשית 99,876.54 תעריף לשעות נוספות 55.55
        |1042 ש.נוספות גלובלי 1,674.67
        |002 ב.מ קצבה זמני קצבה שכיר-תג04/16. 10,000.00- 3,141.59-
        |מס רגיל 2,222
        |002 ב.מ קצבה זמני קצבה שכיר-תג04/16. 10,666.00- 3,141.59-
        |הכנסה לא מבוטחת 2,395
        |002 ב.מ קצבה זמני פיצויים 04/16 10,000.00- 1570.795-
        |ניכוי לסעיף 45א 35% 1000
        |002 ב.מ קצבה זמני פיצויים 04/16 10,000.00- 1570.795-
        |680 ישראל PPSE 1000.00
        |064 מנורה משלימה קצבה שכיר-תג. 5,001.00 6.25 312.56
        |גילום ב.ל. 121
        |064 מנורה משלימה קצבה שכיר-תג. 5,001.00 5.75 287.55
      """.stripMargin
  }

}

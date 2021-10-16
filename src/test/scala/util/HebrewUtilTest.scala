package util

import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit

class HebrewUtilTest extends SpecificationWithJUnit {

  "HebrewUtilTest" should {
    "dateConverter" in new Scope {
      val hebrewDate = "פברואר  2017"
      val englishDate = "February 2017"
      HebrewUtil.dateConvert(hebrewDate) must be_===(englishDate)
    }

    "flipHebrewNotNumbers" in new Ctx {
      HebrewUtil.fixText(hebrewText) must be_===(
        """|002 ב.מ קצבה זמני פיצויים 04/16 10,000.00- 1,234.56-
        |047 הראל קרן פנסיה קצבה שכיר-תג. 11,123.00 5.50 1,098.76
        |ניכוי לסעיף 45א 35% 1,111-
        |680 ישראל ESPP 1,000.00""".stripMargin
      )
    }

  }

  class Ctx extends Scope {
    val hebrewText: String =
      """|-1,234.56 -10,000.00 04/16 םייוציפ ינמז הבצק מ.ב 002
         |1,098.76 5.50 11,123.00 .גת-ריכש הבצק היסנפ ןרק לארה 047
         |-1,111 35% א45 ףיעסל יוכינ
         |1,000.00 ESPP לארשי 680""".stripMargin
  }

}

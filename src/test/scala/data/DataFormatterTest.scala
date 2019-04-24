package data

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


class DataFormatterTest extends SpecificationWithJUnit {

  "DataAggregatorTest" should {

    "format line" in new Scope {
      val value1 = "10,000.00- 3,141.59-"
      val value2 = "10,000.00 6.25 625.00 5.75 575.00"
      val value3 = "123456789 15,000.00 937.5 862.5"
      DataFormatter.formatLine(value1) must be_===(Value(value1, -3141.59, None))
      DataFormatter.formatLine(value2) must be_===(Value(value2, 575, Some(PensionData(625, 575))))
      DataFormatter.formatLine(value3) must be_===(Value(value3, 862.5, Some(PensionData(937.5, 862.5))))
    }

  }

}

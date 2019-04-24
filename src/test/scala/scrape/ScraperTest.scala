package scrape

import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit

class ScraperTest extends SpecificationWithJUnit {

  "ScraperTest" should {
    "extract Dates From Text" in new Ctx {
      val text = "<tr>\n\t\t\t\t\t<td><select id=\"ctl00_mp_Strip___DatePicker_monthPicker\" width=\"101px\" class=\"ComboStyle\">\n\t\t\t\t\t\t<option value=\"01/12/2036\">\n\t\t\t\t\t\t\tדצמבר 2036\n\t\t\t\t\t\t</option><option value=\"01/12/2016\">\n\t\t\t\t\t\t\tדצמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/11/2016\">\n\t\t\t\t\t\t\tנובמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/10/2016\">\n\t\t\t\t\t\t\tאוקטובר 2016\n\t\t\t\t\t\t</option><option value=\"01/09/2016\">\n\t\t\t\t\t\t\tספטמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/08/2016\">\n\t\t\t\t\t\t\tאוגוסט 2016\n\t\t\t\t\t\t</option><option value=\"01/07/2016\">\n\t\t\t\t\t\t\tיולי 2016\n\t\t\t\t\t\t</option><option value=\"01/06/2016\">\n\t\t\t\t\t\t\tיוני 2016\n\t\t\t\t\t\t</option><option value=\"01/05/2016\">\n\t\t\t\t\t\t\tמאי 2016\n\t\t\t\t\t\t</option><option value=\"01/04/2016\">\n\t\t\t\t\t\t\tאפריל 2016\n\t\t\t\t\t\t</option>\n\t\t\t\t\t</select></td>\n\t\t\t\t</tr>\n\t\t\t</table>"
      val dates = scraper.extractDatesFromPage(text)
      val assumedDates = for (i <- 4 to 12) yield HilanDate(1, i, 2016)
      dates must containTheSameElementsAs(assumedDates)
    }

    "extract OrgId From Text" in new Ctx {
      val text = "\\u003c/div\\u003e\\\\r\\\\n\\u003c/div\\u003e\\\\r\\\\n{{/if}}\\\\r\\\\n\\\\r\\\\n\\\",\\\"templateId\\\":null,\\\"initialData\\\":{\\\"OrgId\\\":\\\"2013\\\",\\\"IsShowOrganizationSelection\\\":false,\\\"IsShowId\\\":false,\\\"IsShowOrgLogo\\\":false,\\\"IsShowForgotPassword\\\":true,\\\"IsShowForgotPasswordHelp\\\":true,\\\"OrgName\\\":\\\"WIX\\\",\\\"Culture\\\":\\\"en-US\\\",\\\"SiteType\\\":1,\\\"Code\\\":0,\\\"IsMobileApp\\\":false,\\\"OCookie\\\":null,\\\"ErrorMessage\\\":null,\\\"IsUseIdAsEmployeeId\\\":false,\\\"IsBlocked\\\":false,\\\"VmName\\\":null,\\\"Title\\\":null}}'));"
      val orgId = scraper.extractOrgIdFromPage(text)
      orgId must beSome("2013")
    }

  }

  class Ctx extends Scope {
    val scraper = new Scraper
  }

}

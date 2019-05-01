package clients

import com.gargoylesoftware.htmlunit.WebClient
import org.specs2.matcher.Scope
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit

class AuthenticatedClientTest extends SpecWithJUnit with Mockito {

  "AuthenticatedClient" should {
    "extract Dates From Text" in new Ctx {
      val text = "<tr>\n\t\t\t\t\t<td><select id=\"ctl00_mp_Strip___DatePicker_monthPicker\" width=\"101px\" class=\"ComboStyle\">\n\t\t\t\t\t\t<option value=\"01/12/2036\">\n\t\t\t\t\t\t\tדצמבר 2036\n\t\t\t\t\t\t</option><option value=\"01/12/2016\">\n\t\t\t\t\t\t\tדצמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/11/2016\">\n\t\t\t\t\t\t\tנובמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/10/2016\">\n\t\t\t\t\t\t\tאוקטובר 2016\n\t\t\t\t\t\t</option><option value=\"01/09/2016\">\n\t\t\t\t\t\t\tספטמבר 2016\n\t\t\t\t\t\t</option><option value=\"01/08/2016\">\n\t\t\t\t\t\t\tאוגוסט 2016\n\t\t\t\t\t\t</option><option value=\"01/07/2016\">\n\t\t\t\t\t\t\tיולי 2016\n\t\t\t\t\t\t</option><option value=\"01/06/2016\">\n\t\t\t\t\t\t\tיוני 2016\n\t\t\t\t\t\t</option><option value=\"01/05/2016\">\n\t\t\t\t\t\t\tמאי 2016\n\t\t\t\t\t\t</option><option value=\"01/04/2016\">\n\t\t\t\t\t\t\tאפריל 2016\n\t\t\t\t\t\t</option>\n\t\t\t\t\t</select></td>\n\t\t\t\t</tr>\n\t\t\t</table>"
      val dates = client.extractDatesFromPage(text)
      val assumedDates = for (i <- 4 to 12) yield HilanDate(1, i, 2016)
      dates must containTheSameElementsAs(assumedDates)
    }

  }

  class Ctx extends Scope {
    val username = "boaz"
    val orgId = "123"
    val baseUrl = "http://somewhere.com"
    val webClient = mock[WebClient]
    val client = new AuthenticatedClient(webClient, username, orgId, baseUrl)
  }

}

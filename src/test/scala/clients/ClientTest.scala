package clients

import org.specs2.matcher.Scope
import org.specs2.mutable.SpecWithJUnit

class ClientTest extends SpecWithJUnit {

  "Client" should {

    "extract OrgId From Text" in new Ctx {
      val text = "\\u003c/div\\u003e\\\\r\\\\n\\u003c/div\\u003e\\\\r\\\\n{{/if}}\\\\r\\\\n\\\\r\\\\n\\\",\\\"templateId\\\":null,\\\"initialData\\\":{\\\"OrgId\\\":\\\"2013\\\",\\\"IsShowOrganizationSelection\\\":false,\\\"IsShowId\\\":false,\\\"IsShowOrgLogo\\\":false,\\\"IsShowForgotPassword\\\":true,\\\"IsShowForgotPasswordHelp\\\":true,\\\"OrgName\\\":\\\"WIX\\\",\\\"Culture\\\":\\\"en-US\\\",\\\"SiteType\\\":1,\\\"Code\\\":0,\\\"IsMobileApp\\\":false,\\\"OCookie\\\":null,\\\"ErrorMessage\\\":null,\\\"IsUseIdAsEmployeeId\\\":false,\\\"IsBlocked\\\":false,\\\"VmName\\\":null,\\\"Title\\\":null}}'));"
      val orgId: Option[String] = client.extractOrgIdFromPage(text)
      orgId must beSome("2013")
    }

  }

  class Ctx extends Scope {
    val client = new Client()
  }

}

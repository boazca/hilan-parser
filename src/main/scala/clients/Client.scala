package clients

import java.net.URL

import com.gargoylesoftware.htmlunit.{HttpMethod, Page, WebClient, WebRequest}
import org.slf4j.LoggerFactory
import scrape.NoDataFoundException

import scala.util.Try

class Client() {

  private val logger = LoggerFactory.getLogger(classOf[Client])

  def login(baseUrl: String, username: String, password: String): Try[AuthenticatedClient] = {
    val webClient = new WebClient
    webClient.getOptions.setCssEnabled(false)
    webClient.getOptions.setJavaScriptEnabled(false)

    loginAndGetOrgId(webClient, baseUrl, username, password).map(orgId =>
      new AuthenticatedClient(webClient, username, orgId, baseUrl))
  }

  private def loginAndGetOrgId(webClient: WebClient, baseUrl: String, username: String, password: String): Try[String] = {
    for {
      orgId <- getLoginPageOrgId(webClient, baseUrl)
      _ <- loginAndSetCookies(webClient, baseUrl, username, password, orgId)
    } yield orgId
  }

  private def getLoginPageOrgId(webClient: WebClient, baseUrl: String): Try[String] = {
    val loginPage = Try(webClient.getPage[Page](s"$baseUrl/login").getWebResponse.getContentAsString())
    logger.debug(s"login page: $loginPage")
    loginPage.flatMap(page =>
      Try(extractOrgIdFromPage(page).getOrElse(
        throw new NoDataFoundException("Can't find organization id, probably login page is incorrect")))
    )
  }

  def extractOrgIdFromPage(text: String): Option[String] = {
    val pattern = "\\\\\"OrgId\\\\\":\\\\\"(\\d+)\\\\\"".r
    pattern.findFirstMatchIn(text).map(_.group(1))
  }

  private def loginAndSetCookies(webClient: WebClient, baseUrl: String, username: String, password: String, orgId: String): Try[String] = {
    val loginRequest = new WebRequest(new URL(s"$baseUrl/HilanCenter/Public/api/LoginApi/LoginRequest"), HttpMethod.POST)
    loginRequest.setRequestBody(s"orgId=$orgId&username=$username&password=$password&isEn=true")
    val homePage = Try(webClient.getPage[Page](loginRequest).getWebResponse.getContentAsString())
    logger.debug(s"LoginRequest: $homePage")
    homePage
  }

}

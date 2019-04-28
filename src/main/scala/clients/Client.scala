package clients

import java.net.URL

import com.gargoylesoftware.htmlunit.{HttpMethod, Page, WebClient, WebRequest}
import org.slf4j.LoggerFactory
import scrape.NoDataFoundException

import scala.util.Try

class Client(baseUrl: String, username: String, password: String) {

  private val logger = LoggerFactory.getLogger(classOf[Client])

  val webClient: WebClient = {
    val webClient = new WebClient
    webClient.getOptions.setCssEnabled(false)
    webClient.getOptions.setJavaScriptEnabled(false)
    webClient
  }

  val orgId: Try[String] = loginAndGetOrgId(baseUrl, username, password)

  private def extractOrgIdFromPage(text: String): Option[String] = {
    val pattern = "\\\\\"OrgId\\\\\":\\\\\"(\\d+)\\\\\"".r
    pattern.findFirstMatchIn(text).map(_.group(1))
  }

  private def loginAndSetCookies(baseUrl: String, username: String, password: String, orgId: String) = {
    val loginRequest = new WebRequest(new URL(s"$baseUrl/HilanCenter/Public/api/LoginApi/LoginRequest"), HttpMethod.POST)
    loginRequest.setRequestBody(s"orgId=$orgId&username=$username&password=$password&isEn=true")
    val homePage = Try(webClient.getPage[Page](loginRequest).getWebResponse.getContentAsString())
    logger.debug(s"LoginRequest: $homePage")
    homePage
  }

  private def getLoginPageOrgId(baseUrl: String): Try[String] = {
    val loginPage = Try(webClient.getPage[Page](s"$baseUrl/login").getWebResponse.getContentAsString())
    logger.debug(s"login page: $loginPage")
    loginPage.flatMap(page =>
      Try(extractOrgIdFromPage(page).getOrElse(
        throw new NoDataFoundException("Can't find organization id, probably login page is incorrect")))
    )
  }

  private def loginAndGetOrgId(baseUrl: String, username: String, password: String): Try[String] = {
    for {
      orgId <- getLoginPageOrgId(baseUrl)
      _ <- loginAndSetCookies(baseUrl, username, password, orgId)
    } yield orgId
  }
}

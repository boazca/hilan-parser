package scrape

import java.io.{BufferedOutputStream, FileOutputStream}
import java.net.URL
import java.util.Calendar

import com.gargoylesoftware.htmlunit._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}


class Scraper {
  private val logger = LoggerFactory.getLogger(classOf[Scraper])
  private val client = {
    val webClient = new WebClient
    webClient.getOptions.setCssEnabled(false)
    webClient.getOptions.setJavaScriptEnabled(false)
    webClient
  }

  def downloadPayslips(folderPath: String, baseUrl: String, username: String, password: String): Try[Unit] = {
    for {
      orgId <- getLoginPageOrgId(baseUrl)
      _ <- loginAndSetCookies(baseUrl, username, password, orgId)
      payslipDates <- getAllPayslipsDates(baseUrl, username, orgId)
      downloaded <- downloadToFolder(folderPath, payslipDates, orgId, baseUrl, username)
    } yield downloaded
  }

  private def downloadToFolder(folderPath: String, payslipDates: Seq[HilanDate], orgId: String, baseUrl: String, username: String) = {
    Try(payslipDates.foreach { date =>
      val fileName = s"PaySlip-${date.year}-${date.month}.pdf"
      val fullDate = s"${date.day}/${date.month}/${date.year}"
      val filePath = s"$baseUrl/Hilannetv2/PersonalFile/PdfPaySlip.aspx/$fileName?Date=$fullDate&userId=$orgId$username"
      val payslipFile = client.getPage[Page](filePath).getWebResponse.getContentAsStream
      val localFileDestination = new java.io.File(folderPath + fileName)
      val out = new BufferedOutputStream(new FileOutputStream(localFileDestination))
      val byteArray = Stream.continually(payslipFile.read).takeWhile(_ != -1).map(_.toByte).toArray
      out.write(byteArray)
      out.flush()
      out.close()
      logger.debug(s"finished downloading $fileName")
    })
  }


  private def getAllPayslipsDates(baseUrl: String, username: String, orgId: String) = {
    val pageWithDates = Try(client
      .getPage[Page](s"$baseUrl/Hilannetv2/PersonalFile/PaySlipViewer.aspx?empId=$orgId$username")
      .getWebResponse.getContentAsString())
    logger.debug(s"PaySlipViewer: $pageWithDates")

    pageWithDates.flatMap(page =>
      extractDatesFromPage(page) match {
        case Seq() => Failure(new NoDataFoundException("No payslips found. Login probably failed."))
        case dates => Success(dates)
      }
    )
  }

  private def loginAndSetCookies(baseUrl: String, username: String, password: String, orgId: String) = {
    val loginRequest = new WebRequest(new URL(s"$baseUrl/HilanCenter/Public/api/LoginApi/LoginRequest"), HttpMethod.POST)
    loginRequest.setRequestBody(s"orgId=$orgId&username=$username&password=$password&isEn=true")
    val homePage = Try(client.getPage[Page](loginRequest).getWebResponse.getContentAsString())
    logger.debug(s"LoginRequest: $homePage")
    homePage
  }

  private def getLoginPageOrgId(baseUrl: String): Try[String] = {
    val loginPage = Try(client.getPage[Page](s"$baseUrl/login").getWebResponse.getContentAsString())
    logger.debug(s"login page: $loginPage")
    loginPage.flatMap(page =>
      Try(extractOrgIdFromPage(page).getOrElse(
        throw new NoDataFoundException("Can't find organization id, probably login page is incorrect")))
    )
  }

  def extractDatesFromPage(text: String): Seq[HilanDate] = {
    val pattern = "(\\d{2})/(\\d{2})/(20\\d{2})".r
    val dates = pattern.findAllIn(text).matchData.toSeq
    val hilanDates = dates.map(m => HilanDate(m.group(1).toInt, m.group(2).toInt, m.group(3).toInt))
    hilanDates.filter(_.year <= Calendar.getInstance().get(Calendar.YEAR)) //Don't ask...
  }

  def extractOrgIdFromPage(text: String): Option[String] = {
    val pattern = "\\\\\"OrgId\\\\\":\\\\\"(\\d+)\\\\\"".r
    pattern.findFirstMatchIn(text).map(_.group(1))
  }

}

case class HilanDate(day: Int, month: Int, year: Int)

class NoDataFoundException(msg: String) extends RuntimeException(msg)

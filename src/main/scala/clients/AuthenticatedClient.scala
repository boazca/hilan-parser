package clients

import java.io.InputStream
import java.util.Calendar

import com.gargoylesoftware.htmlunit.{Page, WebClient}
import org.slf4j.LoggerFactory
import scrape.NoDataFoundException

import scala.util.{Failure, Success, Try}


class AuthenticatedClient(webClient: WebClient, username: String, orgId: String, baseUrl: String) {

  private val logger = LoggerFactory.getLogger(classOf[AuthenticatedClient])

  def getAllPayslipsDates(): Try[Seq[HilanDate]] = {
    val pageWithDates = Try(
      webClient
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

  def getAllForm106Dates(): Try[Seq[HilanDate]] = {
    val pageWithDates = Try(
      webClient.getPage[Page](s"$baseUrl/Hilannetv2/PersonalFile/Form106Viewer.aspx?empId=$orgId$username")
        .getWebResponse.getContentAsString())
    logger.debug(s"Form106Viewer: $pageWithDates")

    pageWithDates.flatMap(page => Success(extractDatesFromPage(page)))
  }

  def getPayslipFileStream(fileName: String, fullDate: String): InputStream = {
    val filePath = s"$baseUrl/Hilannetv2/PersonalFile/PdfPaySlip.aspx/$fileName?Date=$fullDate&userId=$orgId$username"
    webClient.getPage[Page](filePath).getWebResponse.getContentAsStream
  }

  def getForm106FileStream(fileName: String, year: Int): InputStream = {
    val filePath = s"$baseUrl/Hilannetv2/PersonalFile/Pdf106.aspx/$fileName?Year=$year&UserId=$orgId$username"
    webClient.getPage[Page](filePath).getWebResponse.getContentAsStream
  }

  def extractDatesFromPage(text: String): Seq[HilanDate] = {
    val pattern = "\"(\\d{2})/(\\d{2})/(20\\d{2})\"".r
    val dates = pattern.findAllIn(text).matchData.toSeq
    val hilanDates = dates.map(m => HilanDate(m.group(1).toInt, m.group(2).toInt, m.group(3).toInt))
    hilanDates.filter(_.year <= Calendar.getInstance().get(Calendar.YEAR)) //Don't ask...
  }

}

case class HilanDate(day: Int, month: Int, year: Int)

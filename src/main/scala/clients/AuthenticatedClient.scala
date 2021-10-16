package clients

import java.io.InputStream
import java.net.URL

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.gargoylesoftware.htmlunit.{HttpMethod, Page, WebClient, WebRequest}
import org.slf4j.LoggerFactory
import scrape.NoDataFoundException

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}


class AuthenticatedClient(webClient: WebClient, username: String, orgId: String, baseUrl: String) {

  private val logger = LoggerFactory.getLogger(classOf[AuthenticatedClient])
  private val mapper = new ObjectMapper

  def fetchAllPayslipsDates(): Try[Seq[HilanDate]] = {
    val request = new WebRequest(new URL(s"$baseUrl/Hilannetv2/Services/Public/WS/PaySlipApiapi.asmx/GetInitialData"), HttpMethod.POST)
    request.setRequestBody(s"""{"employeeId":"$username"}""")
    val pageWithDates = Try(webClient.getPage[Page](request).getWebResponse.getContentAsString())
    logger.debug(s"PaySlipViewer: $pageWithDates")

    pageWithDates.flatMap(page =>
      getDatesFromPaySlipJson(page) match {
        case Seq() => Failure(new NoDataFoundException("No payslips found. Login probably failed."))
        case dates => Success(dates)
      }
    )
  }

  def fetchAllForm106Dates(): Try[Seq[HilanDate]] = {
    val request = new WebRequest(new URL(s"$baseUrl/Hilannetv2/Services/Public/WS/Form106Apiapi.asmx/GetInitialData"), HttpMethod.POST)
    request.setRequestBody(s"""{"employeeId":"$username"}""")
    val pageWithDates = Try(webClient.getPage[Page](request).getWebResponse.getContentAsString())
    logger.debug(s"Form106Viewer: $pageWithDates")

    pageWithDates.flatMap(page => Success(getDatesFrom106Json(page)))
  }

  def getPayslipFileStream(fileName: String, fullDate: String): InputStream = {
    val filePath = s"$baseUrl/Hilannetv2/PersonalFile/PdfPaySlip.aspx/$fileName?Date=$fullDate&userId=$orgId$username"
    webClient.getPage[Page](filePath).getWebResponse.getContentAsStream
  }

  def getForm106FileStream(fileName: String, year: Int): InputStream = {
    val filePath = s"$baseUrl/Hilannetv2/PersonalFile/Pdf106.aspx/$fileName?Year=$year&UserId=$orgId$username"
    webClient.getPage[Page](filePath).getWebResponse.getContentAsStream
  }

  private def getDatesFromPaySlipJson(jsonString: String): Seq[HilanDate] = {
    val json = mapper.readTree(jsonString)
    json.get("PaySlipDates").iterator.asScala.map((date: JsonNode) => {
      val parts = date.get("Id").asText.split("/")
      HilanDate(1, parts(0).toInt, parts(1).toInt)
    }).toList
  }

  private def getDatesFrom106Json(jsonString: String): Seq[HilanDate] = {
    val json = mapper.readTree(jsonString)
    json.get("Dates").iterator.asScala.map((date: JsonNode) => {
      val year = date.asText.toInt
      HilanDate(1, 1, year)
    }).toList
  }

}

case class HilanDate(day: Int, month: Int, year: Int)

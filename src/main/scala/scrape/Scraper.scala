package scrape

import java.io.{BufferedOutputStream, FileOutputStream}
import java.nio.file.{Files, Paths}
import java.util.Calendar

import clients.Client
import com.gargoylesoftware.htmlunit._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class Scraper(baseUrl: String, username: String, password: String) {
  private val logger = LoggerFactory.getLogger(classOf[Scraper])
  private val client = new Client(baseUrl, username, password)

  def downloadPayslips(folderPath: String, baseUrl: String, username: String): Try[Unit] = {
    for {
      orgId <- client.orgId
      payslipDates <- getAllPayslipsDates(baseUrl, username, orgId)
      downloaded <- downloadPayslipsToFolder(folderPath, payslipDates, orgId, baseUrl, username)
    } yield downloaded
  }

  def downloadForm106s(folderPath: String, baseUrl: String, username: String): Try[Unit] = {
    for {
      orgId <- client.orgId
      form106Dates <- getAllForm106Dates(baseUrl, username, orgId)
      downloaded <- downloadForm106sToFolder(Paths.get(folderPath, "Form106").toString, form106Dates, orgId, baseUrl, username)
    } yield downloaded
  }

  private def downloadPayslipsToFolder(folderPath: String, payslipDates: Seq[HilanDate], orgId: String, baseUrl: String, username: String) = {
    Try(payslipDates.foreach { date =>
      val fileName = s"PaySlip-${date.year}-${date.month}.pdf"
      val fullDate = s"${date.day}/${date.month}/${date.year}"
      val filePath = s"$baseUrl/Hilannetv2/PersonalFile/PdfPaySlip.aspx/$fileName?Date=$fullDate&userId=$orgId$username"
      downloadFile(folderPath, fileName, filePath)
    })
  }

  private def downloadForm106sToFolder(folderPath: String, form106Dates: Seq[HilanDate], orgId: String, baseUrl: String, username: String) = {
    Try(form106Dates.foreach { date =>
      val fileName = s"Form106-${date.year}.pdf"
      val filePath = s"$baseUrl/Hilannetv2/PersonalFile/PdfForm106.aspx/$fileName?Date=${date.year}&userId=$orgId$username"
      downloadFile(folderPath, fileName, filePath)
    })
  }

  private def downloadFile(localFolderPath: String, localFileName: String, remoteFile: String): Unit = {
    if (!Files.isDirectory(Paths.get(localFolderPath))) Files.createDirectory(Paths.get(localFolderPath))
    val sourceFile = client.webClient.getPage[Page](remoteFile).getWebResponse.getContentAsStream
    val localFileDestination = new java.io.File(Paths.get(localFolderPath, localFileName).toString)
    val out = new BufferedOutputStream(new FileOutputStream(localFileDestination))
    val byteArray = Stream.continually(sourceFile.read).takeWhile(_ != -1).map(_.toByte).toArray
    out.write(byteArray)
    out.flush()
    out.close()
    logger.debug(s"finished downloading $localFileName")
  }

  private def getAllPayslipsDates(baseUrl: String, username: String, orgId: String) = {
    val pageWithDates = Try(client.webClient
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

  private def getAllForm106Dates(baseUrl: String, username: String, orgId: String) = {
    val pageWithDates = Try(client
      .webClient.getPage[Page](s"$baseUrl/Hilannetv2/PersonalFile/Form106Viewer.aspx?empId=$orgId$username")
      .getWebResponse.getContentAsString())
    logger.debug(s"Form106Viewer: $pageWithDates")

    pageWithDates.flatMap(page => Success(extractDatesFromPage(page)))
  }

  def extractDatesFromPage(text: String): Seq[HilanDate] = {
    val pattern = "(\\d{2})/(\\d{2})/(20\\d{2})".r
    val dates = pattern.findAllIn(text).matchData.toSeq
    val hilanDates = dates.map(m => HilanDate(m.group(1).toInt, m.group(2).toInt, m.group(3).toInt))
    hilanDates.filter(_.year <= Calendar.getInstance().get(Calendar.YEAR)) //Don't ask...
  }

}

case class HilanDate(day: Int, month: Int, year: Int)

class NoDataFoundException(msg: String) extends RuntimeException(msg)
